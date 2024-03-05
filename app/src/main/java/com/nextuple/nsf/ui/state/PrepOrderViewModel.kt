package com.nextuple.nsf.ui.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.RecordHoldingLocationRequest
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.InfoService
import com.nextuple.nsf.service.PackTaskService
import com.nextuple.nsf.service.StageTaskService
import com.nextuple.nsf.ui.screen.prep.PackType
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.state.InfoViewModel.PrepStage
import com.nextuple.nsf.ui.state.InfoViewModel.PrepStage.Landing
import com.nextuple.nsf.ui.state.InfoViewModel.PrepStage.StageOrder
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrepOrderViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle,
	private val packTaskService: PackTaskService,
	private val stageTaskService: StageTaskService,
	private val infoService: InfoService
) : ViewModel() {
	var prepOrderUiData: PrepOrderUiData by mutableStateOf(PrepOrderUiData())
		private set

	var holdSlipState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var holdLocationState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var packTask: PackTask? by mutableStateOf(null)
		private set

	var stageTask: StageTask? by mutableStateOf(null)
		private set

	var errMsg: String? by mutableStateOf(null)
		private set

	val currentPrepStage = derivedStateOf {
		when {
			stageTask != null && stageTask?.status?.name == "COMPLETED" -> Landing
			stageTask != null && stageTask?.status?.name != "COMPLETED" -> StageOrder
			packTask != null -> PrepStage.PrepOrder
			else -> Landing
		}
	}

	private fun onStoreOverview(res: StoreOverviewResponse?) {
		packTask = res?.userOverview?.currentPackTask
		stageTask = res?.userOverview?.currentStageTask
	}

	fun setUiData(packType: PackType, data: String) = viewModelScope.launch {
		prepOrderUiData = PrepOrderUiData(state = GenericViewState.Loading)

		val res = when (packType) {
			PackType.GEAR -> packTaskService.packByGear(upc = data)
			PackType.ORDER -> packTaskService.getPrepDetails(frNo = data)
		}

		// TODO: Refactor this away when InfoVM, PrepVM, and PrepOrderVM are fully decoupled.
		val overview = when (val response = infoService.getStoreOverview()) {
			is Result.Success -> {
				response.data
			}

			is Result.Error -> {
				prepOrderUiData = PrepOrderUiData(state = GenericViewState.Failure)
				return@launch
			}
		}
		onStoreOverview(overview)

		prepOrderUiData = when (res) {
			is Result.Success -> {
				val packItems = res.data?.items?.flatMap { packTaskItem ->
					List(packTaskItem.qty) { packTaskItem }
				}.orEmpty()
				PrepOrderUiData(
					state = GenericViewState.Success,
					prepOrder = if (res.data?.isInProgress() == true) { // TODO: Update back-end to abstract this.
						// Only set the prep order if the associated task is assigned/in-progress (OpTaskStatusService.Status enum)
						PrepOrder(
							subFulfillmentType = res.data.subFulfillmentType,
							athleteName = "${res.data.athleteFirstName} ${res.data.athleteLastName}",
							orderNumber = res.data.orderNumber.orEmpty(),
							pickedBy = res.data.pickedBy.orEmpty(),
							packItems = packItems
						)
					} else {
						null
					}
				)
			}
			is Result.Error -> {
				PrepOrderUiData(state = GenericViewState.Failure)
			}
		}

		if (packType == PackType.GEAR && prepOrderUiData.prepOrder?.subFulfillmentType != "BOPL") {
			packItem(scannedUpc = data)
		}
	}

	fun packItem(scannedUpc: String): Boolean {
		var success = false

		val updatedPackItems = prepOrderUiData.prepOrder?.packItems?.map {
			if (it.scannedBarcode == scannedUpc && !it.isScanned && !success) {
				success = true
				it.copy(isScanned = true)
			} else {
				it
			}
		}.orEmpty()

		prepOrderUiData = prepOrderUiData.copy(
			prepOrder = prepOrderUiData.prepOrder?.copy(
				packItems = updatedPackItems
			)
		)

		return success
	}

	fun packAndGetHoldSlip() {
		holdSlipState = GenericViewState.Loading
		viewModelScope.launch {
			stageTask = when (
				val response = packTaskService.packAndGetHoldSlip(
					taskId = packTask?.id?.toString() ?: ""
				)
			) {
				is Result.Success -> {
					errMsg = null
					holdSlipState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					if (response.type == Result.ErrorType.NOT_FOUND) {
						holdSlipState = GenericViewState.Failure
					} else {
						resetHoldSlipState()
						/*
							TODO: Display Toast Error Message From View
						 */
					}
					null
				}
			}
		}
	}

	fun recordHoldingLocation(containerId: Long, holdingLocation: String) {
		viewModelScope.launch {
			holdLocationState = GenericViewState.Loading
			val recordHoldingLocationRequest = RecordHoldingLocationRequest(
				containerId = containerId,
				holdingLocation = holdingLocation
			)
			stageTask = when (
				val response =
					stageTaskService.recordHoldingLocation(recordHoldingLocationRequest)
			) {
				is Result.Success -> {
					errMsg = null
					holdLocationState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					if (response.type == Result.ErrorType.NOT_FOUND) {
						holdLocationState = GenericViewState.Failure
					} else {
						resetHoldLocationState()
						/*
							TODO: Display Toast Error Message From View
						 */
					}
					null
				}
			}
		}
	}

	fun resetHoldSlipState() {
		holdSlipState = GenericViewState.Idle
	}

	fun resetHoldLocationState() {
		holdLocationState = GenericViewState.Idle
	}
}

data class PrepOrderUiData(
	val state: GenericViewState = GenericViewState.Idle,
	val prepOrder: PrepOrder? = null
)

data class PrepOrder(
	val subFulfillmentType: String,
	val athleteName: String,
	val orderNumber: String,
	val pickedBy: String,
	var packItems: List<PackTaskItem> = emptyList()
)
