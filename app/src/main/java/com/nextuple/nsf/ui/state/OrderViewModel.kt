package com.nextuple.nsf.ui.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.RecordHoldingLocationRequest
import com.nextuple.nsf.retrofit.dto.response.DeclineCode
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.LogService.Companion.EVENT_AGED_ORDER_CANCEL
import com.nextuple.nsf.service.LogService.Companion.EVENT_ORDER_CANCEL
import com.nextuple.nsf.service.OrderService
import com.nextuple.nsf.service.StageTaskService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.component.filter.Filter
import com.nextuple.nsf.ui.component.filter.toSelectedValues
import com.nextuple.nsf.ui.component.filter.toUpdated
import com.nextuple.nsf.ui.screen.order.OrderScreenTab
import com.nextuple.nsf.ui.screen.order.OrderScreenTab.READY
import com.nextuple.nsf.ui.util.DeclineAction
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.util.OrderStatus
import com.nextuple.nsf.util.SubFulfillmentType
import com.nextuple.nsf.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle,
	private val orderService: OrderService,
	private val stageTaskService: StageTaskService,
	private val logService: LogService
) : ViewModel() {

	companion object {
		private val ORDER_TYPES = listOf(
			"bopis",
			"bopl"
		)
		private val ORDER_STATUSES = listOf(
			"pack",
			"stage",
			"ready",
			"aged",
			"canceled",
			"extended",
			"completed"
		)

		private val ORDER_TYPE_FILTERS_INITIAL = ORDER_TYPES.map { Filter(it) }
		private val ORDER_STATUS_FILTERS_INITIAL = ORDER_STATUSES.map { Filter(it) }
	}

	var viewState: GenericViewState by mutableStateOf(GenericViewState.Loading)
		private set

	var completeOrderPickupState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var orderDetailsState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var startPickupState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var getHoldSlipState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var holdSlipScanState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var cancelReasonData: CancelReasonData by mutableStateOf(CancelReasonData())
		private set

	var errMsg: String? by mutableStateOf(null)
		private set

	var orderDetailResponse: OrderDetailsResponse? by mutableStateOf(null)
		private set

	val orderDate: String by derivedStateOf {
		formatDate(
			timestamp = orderDetailResponse?.orderDate,
			errorName = "OrderDetailResponse_OrderDate"
		)
	}
	val orderTime: String by derivedStateOf {
		formatTime(
			timestamp = orderDetailResponse?.orderDate,
			errorName = "OrderDetailResponse_OrderTime"
		)
	}
	val packDate: String by derivedStateOf {
		formatDate(
			timestamp = orderDetailResponse?.packedOnDate,
			errorName = "OrderDetailResponse_PackDate"
		)
	}
	val packTime: String by derivedStateOf {
		formatTime(
			timestamp = orderDetailResponse?.packedOnDate,
			errorName = "OrderDetailResponse_PackTime"
		)
	}

	val expectedDate: String by derivedStateOf {
		formatTimeStamp(
			timestamp = orderDetailResponse?.expectedDeliveryDate,
			errorName = "OrderDetailResponse_ExpectedDate"
		)
	}

	val receivedDate: String by derivedStateOf {
		formatTimeStamp(
			timestamp = orderDetailResponse?.receivedDate,
			errorName = "OrderDetailResponse_ReceivedDate"
		)
	}

	val pickedUpOnDate: String by derivedStateOf {
		formatDate(
			timestamp = orderDetailResponse?.pickedUpDate,
			errorName = "OrderDetailResponse_PickedUpOnDate"
		)
	}
	val pickedUpOnTime: String by derivedStateOf {
		formatTime(
			timestamp = orderDetailResponse?.pickedUpDate,
			errorName = "OrderDetailResponse_PickedUpOnDate"
		)
	}

	var holdSlipZpl: MutableList<String>? by mutableStateOf(null)
		private set

	var scanLocationState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	private var orderList: List<OrderDetailsResponse>? by mutableStateOf(null)

	val readyOrders by derivedStateOf {
		orderList.orEmpty().filter {
			OrderStatus.isReadyStatusText(it.orderStatusText)
//					&& !(
//							it.fulfillmentRequestDetail.subFulfillmentType == SubFulfillmentType.SAME_DAY.name
//									&& OrderStatus.isCheckedInStatus(it.orderStatusText)
//					)
		}
	}
	private val sddReadyOrders by derivedStateOf {
		orderList.orEmpty().filter {
			it.fulfillmentRequestDetail.subFulfillmentType == SubFulfillmentType.SAME_DAY.name && OrderStatus.isCheckedInStatus(
				it.orderStatusText
			)
		}
	}
	val sddOrderByBatchIds by derivedStateOf {
		sddReadyOrders.groupBy { it.driverDetail?.batchId }.map { it.value }
	}

	val inProgressOrders by derivedStateOf {
		orderList.orEmpty().filter { !OrderStatus.isReadyStatusText(it.orderStatusText) }
	}

	var orderTypeFilters by mutableStateOf(ORDER_TYPE_FILTERS_INITIAL)
		private set
	var orderStatusFilters by mutableStateOf(ORDER_STATUS_FILTERS_INITIAL)
		private set

	var selectedTab by mutableStateOf(READY)
		private set

	fun getOrders(query: String? = null) = viewModelScope.launch {
		viewState = GenericViewState.Loading
		orderList = when (
			val res = orderService.getOrders(
				query = query,
				orderTypeFilter = orderTypeFilters.toSelectedValues(),
				orderStatusFilter = orderStatusFilters.toSelectedValues(),
				minOrderStatus = OrderStatus.PACK.statusText
			)
		) {
			is Result.Success -> {
				errMsg = null
				viewState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				viewState = GenericViewState.Failure
				null
			}
		}

		if (orderList != null) {
			selectedTab = OrderScreenTab.determineSelectedTab(
				current = selectedTab,
				hasReadyOrders = readyOrders.isNotEmpty(),
				hasInProgressOrders = inProgressOrders.isNotEmpty(),
				selectedFilters = orderStatusFilters.toSelectedValues()
			)
		}
	}

	fun setFilters(orderTypeFilters: List<Filter>, orderStatusFilters: List<Filter>) {
		this.orderTypeFilters = orderTypeFilters
		this.orderStatusFilters = orderStatusFilters
	}

	fun setFilter(filter: Filter) {
		orderTypeFilters = orderTypeFilters.toUpdated(filter)
		orderStatusFilters = orderStatusFilters.toUpdated(filter)
	}

	fun resetFilters() {
		orderTypeFilters = ORDER_TYPE_FILTERS_INITIAL
		orderStatusFilters = ORDER_STATUS_FILTERS_INITIAL
	}

	fun setTab(tab: OrderScreenTab) {
		selectedTab = tab
	}

	fun getOrderDetails(fulfillmentRequestNumber: String) = viewModelScope.launch {
		orderDetailsState = GenericViewState.Loading
		orderDetailResponse =
			when (val res = orderService.getOrderDetails(fulfillmentRequestNumber)) {
				is Result.Success -> {
					errMsg = null
					orderDetailsState = GenericViewState.Success
					res.data
				}

				is Result.Error -> {
					errMsg = res.msg
					orderDetailsState = GenericViewState.Failure
					null
				}
			}
	}

	fun cancelOrder(
		fulfillmentRequestNumber: String,
		declinedReason: DeclineCode,
		shouldTranslateReason: Boolean = false
	) = viewModelScope.launch {
		cancelReasonData = CancelReasonData(GenericViewState.Loading)
		val res = orderService.recordDecline(
			fulfillmentRequestNumber = fulfillmentRequestNumber,
			declinedReason = declinedReason.id,
			shouldTranslateReason = shouldTranslateReason,
			action = DeclineAction.PICKUP_DECLINE.name
		)
		when (res) {
			is Result.Success -> {
				cancelReasonData = CancelReasonData(
					state = GenericViewState.Success,
					isDamaged = declinedReason.id == "DAMAGE"
				)
			}

			is Result.Error -> {
				cancelReasonData = CancelReasonData(GenericViewState.Failure)
				logService.trackError(
					EVENT_ORDER_CANCEL,
					Throwable(res.msg),
					mapOf(
						"Order Number" to orderDetailResponse?.orderNumber.orEmpty(),
						"FR Details" to orderDetailResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber.orEmpty()
					)
				)
			}
		}
	}

	fun cancelAgedOrderError(error: String) {
		logService.trackError(
			EVENT_AGED_ORDER_CANCEL,
			Throwable(error),
			mapOf(
				"Order Number" to orderDetailResponse?.orderNumber.orEmpty(),
				"FR Details" to orderDetailResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber.orEmpty()
			)
		)
	}

	fun pickupExtend(fulfillmentRequestNumber: String) = viewModelScope.launch {
		viewState = GenericViewState.Loading
		orderDetailResponse = when (
			val res =
				orderService.pickupExtend(fulfillmentRequestNumber = fulfillmentRequestNumber)
		) {
			is Result.Success -> {
				errMsg = null
				viewState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				viewState = GenericViewState.Failure
				null
			}
		}
	}

	fun pickupRemoveCheckIn(taskId: String) = viewModelScope.launch {
		viewState = GenericViewState.Loading
		orderDetailResponse = when (val res = orderService.pickupRemoveCheckIn(taskId = taskId)) {
			is Result.Success -> {
				errMsg = null
				viewState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				viewState = GenericViewState.Failure
				null
			}
		}
	}

	fun startPickup(fulfillmentRequestNumber: String) = viewModelScope.launch {
		startPickupState = GenericViewState.Loading
		orderDetailResponse = when (
			val res =
				orderService.startPickupTask(fulfillmentRequestNumber = fulfillmentRequestNumber)
		) {
			is Result.Success -> {
				errMsg = null
				startPickupState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				startPickupState = GenericViewState.Failure
				null
			}
		}
	}

	fun completePickupTask(taskId: String) = viewModelScope.launch {
		completeOrderPickupState = GenericViewState.Loading
		orderDetailResponse =
			when (val res = orderService.completePickupTask(taskId = taskId)) {
				is Result.Success -> {
					errMsg = null
					completeOrderPickupState = GenericViewState.Success
					res.data
				}

				is Result.Error -> {
					errMsg = res.msg
					completeOrderPickupState = GenericViewState.Failure
					null
				}
			}
	}

	fun getHoldSlip(fulfillmentRequestNumber: String) = viewModelScope.launch {
		getHoldSlipState = GenericViewState.Loading
		holdSlipZpl =
			when (
				val res =
					stageTaskService.getHoldSlip(fulfillmentRequestNumber = fulfillmentRequestNumber)
			) {
				is Result.Success -> {
					errMsg = null
					getHoldSlipState = GenericViewState.Success
					res.data?.holdSlipZPL
				}

				is Result.Error -> {
					errMsg = res.msg
					getHoldSlipState = GenericViewState.Failure
					null
				}
			}
	}

	fun holdSuccessSlipScan(scanData: String) {
		holdSlipScanState = GenericViewState.Loading
		if (scanData == orderDetailResponse?.orderNumber) {
			holdSlipScanState = GenericViewState.Success
		} else {
			holdSlipScanState = GenericViewState.Failure
		}
	}

	fun recordHoldingLocation(containerId: Long, holdingLocation: String) {
		viewModelScope.launch {
			scanLocationState = GenericViewState.Loading
			val recordHoldingLocationRequest = RecordHoldingLocationRequest(
				containerId = containerId,
				holdingLocation = holdingLocation
			)

			when (
				val response =
					stageTaskService.recordHoldingLocation(recordHoldingLocationRequest)
			) {
				is Result.Success -> {
					errMsg = null
					scanLocationState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					if (response.type == Result.ErrorType.NOT_FOUND) {
						scanLocationState = GenericViewState.Failure
					} else if (errMsg?.contains("should be in IN_PROGRESS") == true) {
						errMsg = null
						scanLocationState = GenericViewState.Success
					} else {
						resetScanLocationState()
						/*
							TODO: Display Toast Error Message From View
						 */
					}
				}
			}
		}
	}

	/**
	 * Removing existing state and default to default state.
	 */
	fun resetCompleteOrderPickState() {
		completeOrderPickupState = GenericViewState.Idle
	}

	/**
	 * Removing existing orderDetailsState state and default to default state.
	 */
	fun resetOrderDetailsState() {
		orderDetailsState = GenericViewState.Idle
	}

	/**
	 * Removing existing startPickupState state and default to default state.
	 */
	fun resetStarPickupState() {
		startPickupState = GenericViewState.Idle
	}

	/**
	 * Removing existing holdSlip state and default to default state.
	 */
	fun resetGetHoldSlipState() {
		getHoldSlipState = GenericViewState.Idle
	}

	/**
	 * Removing existing holdSlipScan state and default to default state.
	 */
	fun resetHoldSlipScanState() {
		holdSlipScanState = GenericViewState.Idle
	}

	/**
	 * Removing existing scanLocation state and default to default state.
	 */
	fun resetScanLocationState() {
		scanLocationState = GenericViewState.Idle
	}

	/**
	 * Removing existing cancel state and default to default state.
	 */
	fun resetCancelReasonData() {
		cancelReasonData = CancelReasonData()
	}

	private fun formatDate(timestamp: String?, errorName: String): String = runCatching {
		val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
		df.timeZone = TimeZone.getTimeZone("UTC")
		val date = timestamp?.let { df.parse(it) }
		val dateSplit = date.toString().split(" ")
		dateSplit[1] + " " + dateSplit[2] + ", " + dateSplit[5]
	}.onFailure {
		logService.trackError(errorName, it)
	}.getOrNull().orEmpty()

	private fun formatTime(timestamp: String?, errorName: String): String = runCatching {
		val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
		df.timeZone = TimeZone.getTimeZone("UTC")
		val date = timestamp?.let { df.parse(it) }
		val dateSplit = date.toString().split(" ")
		dateSplit[3] + " " + dateSplit[4]
	}.onFailure {
		logService.trackError(errorName, it)
	}.getOrNull().orEmpty()

	private fun formatTimeStamp(timestamp: String?, errorName: String): String = runCatching {
		timestamp?.ifEmpty { null }?.let {
			TimeUtils.formatTimeStamp(Instant.parse(it))
		}
	}.onFailure {
		logService.trackError(errorName, it)
	}.getOrNull().orEmpty()
}

data class CancelReasonData(
	val state: GenericViewState = GenericViewState.Idle,
	val isDamaged: Boolean = false
)
