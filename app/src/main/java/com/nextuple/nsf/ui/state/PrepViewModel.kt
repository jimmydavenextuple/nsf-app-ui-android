package com.nextuple.nsf.ui.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.FITTask
import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.PrepDetail
import com.nextuple.nsf.retrofit.dto.RecordDeclineRequest
import com.nextuple.nsf.retrofit.dto.RecordHoldingLocationRequest
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.service.InfoService
import com.nextuple.nsf.service.PackTaskService
import com.nextuple.nsf.service.StageTaskService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.screen.prep.PackType
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Assemble
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Landing
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Pack
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Stage
import com.nextuple.nsf.ui.util.DeclineAction
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.util.SubFulfillmentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrepViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle,
	private val infoService: InfoService,
	private val packService: PackTaskService,
	private val stageService: StageTaskService
) : ViewModel() {

	sealed class Step {
		data object Landing : Step()
		data object Assemble : Step()
		data object Pack : Step()
		data object Stage : Step()
	}

	data class PrepOrder(
		val subFulfillmentType: String,
		val athleteName: String,
		val orderNumber: String,
		val pickedBy: String,
		var packItems: List<PackTaskItem> = emptyList(),
		val holdSlipZPL: MutableList<String> = mutableListOf()
	)

	var viewState by mutableStateOf<GenericViewState>(GenericViewState.Loading)
		private set
	var packTask: PackTask? by mutableStateOf(null)
		private set
	var packDeclineState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var stageTask: StageTask? by mutableStateOf(null)
		private set

	val currentStep by derivedStateOf {
		when {
			stageTask != null && stageTask?.status?.name == "COMPLETED" -> Landing
			stageTask != null && stageTask?.status?.name != "COMPLETED" -> Stage
			packTask != null && FITTask.isInProgress(packTask?.status?.code.orEmpty()) -> Pack
			packTask != null -> Assemble
			else -> Landing
		}
	}

	var prepOrder: PrepOrder? by mutableStateOf(null)
	var getHoldSlipState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set
	var holdLocationState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set
	var startPackAndGetHoldSlipState by mutableStateOf<GenericViewState>(GenericViewState.Idle)
		private set

	fun fetchCurrentStep() = viewModelScope.launch {
		viewState = GenericViewState.Loading
		when (val response = infoService.getUserPrepTasks()) {
			is Result.Success -> {
				val userPrepTasks = response.data
				packTask = userPrepTasks?.packTask
				stageTask = userPrepTasks?.stageTask
			}

			is Result.Error -> {
				packTask = null
				stageTask = null
				viewState = GenericViewState.Failure
				return@launch
			}
		}
		if (currentStep == Landing) {
			viewState = GenericViewState.Success
			return@launch
		}
		val frNo = stageTask?.fulfillmentRequestNumber?.ifEmpty { null }
			?: packTask?.fulfillmentRequestNumber?.ifEmpty { null }
		if (frNo != null) {
			fetchPrepOrder(packType = PackType.ORDER, upcOrFrNo = frNo)
		}
	}

	fun fetchPrepOrder(packType: PackType, upcOrFrNo: String) = viewModelScope.launch {
		viewState = GenericViewState.Loading
		val res = stageTask?.let {
			packService.getPrepDetails(frNo = it.fulfillmentRequestNumber)
		} ?: when (packType) {
			PackType.GEAR -> packService.packByGear(upc = upcOrFrNo)
			PackType.ORDER -> packService.packByOrder(frNo = upcOrFrNo)
		}
		prepOrder = when (res) {
			is Result.Success -> {
				if (res.data == null) {
					viewState = GenericViewState.Failure
					return@launch
				}
				updateUserPrepTasks(
					frNo = res.data.fulfillmentRequestNumber,
					pack = res.data.packTask,
					stage = res.data.stageTask
				)
				toPrepOrder(res.data)
			}

			is Result.Error -> {
				viewState = GenericViewState.Failure
				return@launch
			}
		}
		if (packType == PackType.GEAR && prepOrder?.subFulfillmentType != SubFulfillmentType.BOPL.name) {
			packItem(scannedUpc = upcOrFrNo)
		}
		viewState = GenericViewState.Success
	}

	fun packItem(scannedUpc: String): Boolean {
		var success = false
		val updatedPackItems = prepOrder?.packItems?.map {
			if (it.scannedBarcode == scannedUpc && !it.isScanned && !it.isDeclined && !success) {
				success = true
				it.copy(isScanned = true)
			} else {
				it
			}
		}.orEmpty()
		prepOrder = prepOrder?.copy(packItems = updatedPackItems)
		return success
	}

	fun startPackAndGetHoldSlip() {
		startPackAndGetHoldSlipState = GenericViewState.Loading
		viewModelScope.launch {
			prepOrder = when (
				val res = packService.startPackTask(taskId = packTask?.id?.toString() ?: "")
			) {
				is Result.Success -> {
					if (res.data == null) {
						startPackAndGetHoldSlipState = GenericViewState.Failure
						return@launch
					}
					updateUserPrepTasks(
						frNo = res.data.fulfillmentRequestNumber,
						pack = res.data.packTask,
						stage = res.data.stageTask
					)
					toPrepOrder(res.data)
				}

				is Result.Error -> {
					startPackAndGetHoldSlipState = GenericViewState.Failure
					null
				}
			}
			startPackAndGetHoldSlipState = GenericViewState.Success
		}
	}

	fun completePack() = packAndGetHoldSlip(isPackStarted = true)
	fun packAndGetHoldSlip() = packAndGetHoldSlip(isPackStarted = false)
	private fun packAndGetHoldSlip(isPackStarted: Boolean) {
		getHoldSlipState = GenericViewState.Loading
		viewModelScope.launch {
			val taskId = packTask?.id?.toString() ?: ""
			val res = if (isPackStarted) {
				packService.completePackAndGetHoldSlip(taskId = taskId)
			} else {
				packService.packAndGetHoldSlip(taskId = taskId)
			}
			stageTask = when (res) {
				is Result.Success -> {
					getHoldSlipState = GenericViewState.Success
					res.data
				}

				is Result.Error -> {
					if (res.type == Result.ErrorType.NOT_FOUND) {
						getHoldSlipState = GenericViewState.Failure
					} else {
						resetGetHoldSlipState()
					}
					null
				}
			}
		}
	}

	fun declinePackItem(declineReason: String, indexToDecline: Int, itemToDecline: PackTaskItem) {
		packDeclineState = GenericViewState.Loading
		viewModelScope.launch {
			val declineItemRequest = RecordDeclineRequest(
				fulfillmentRequestNumber = packTask!!.fulfillmentRequestNumber,
				sku = itemToDecline.originalItem?.sku ?: itemToDecline.sku,
				declinedUnits = 1,
				declinedReason = declineReason,
				shouldTranslateReason = false,
				action = DeclineAction.PACK_DECLINE.toString()
			)

			packDeclineState = when (packService.declinePackItem(req = declineItemRequest)) {
				is Result.Success -> {
					val updatedPackItems = prepOrder?.packItems?.mapIndexed { i, item ->
						if (i == indexToDecline && item.scannedBarcode == itemToDecline.scannedBarcode) {
							item.copy(isDeclined = true)
						} else {
							item
						}
					}.orEmpty()
					prepOrder = prepOrder?.copy(packItems = updatedPackItems)
					GenericViewState.Success
				}

				is Result.Error -> {
					GenericViewState.Failure
				}
			}
		}
	}

	fun recordHoldingLocation(containerId: Long, holdingLocation: String) {
		holdLocationState = GenericViewState.Loading
		viewModelScope.launch {
			val recordHoldingLocationRequest = RecordHoldingLocationRequest(
				containerId = containerId,
				holdingLocation = holdingLocation
			)
			when (val response = stageService.recordHoldingLocation(recordHoldingLocationRequest)) {
				is Result.Success -> {
					holdLocationState = GenericViewState.Success
				}

				is Result.Error -> {
					if (response.type == Result.ErrorType.NOT_FOUND) {
						holdLocationState = GenericViewState.Failure
					} else {
						resetHoldLocationState()
					}
				}
			}
		}
	}

	fun resetGetHoldSlipState() {
		getHoldSlipState = GenericViewState.Idle
	}

	fun resetHoldLocationState() {
		holdLocationState = GenericViewState.Idle
	}

	fun resetPrepState() {
		stageTask = null
		packTask = null
		prepOrder = null
		viewState = GenericViewState.Success
	}

	/**
	 * TODO: Refactor away the need for this.
	 */
	private fun updateUserPrepTasks(frNo: String, pack: FITTask?, stage: FITTask?) {
		stageTask = stageTask?.copy(status = stage?.status)
		packTask = pack?.let {
			PackTask(
				id = it.taskId ?: -1L,
				status = it.status,
				fulfillmentRequestNumber = frNo
			)
		}
	}

	private fun toPrepOrder(res: PrepDetail): PrepOrder = PrepOrder(
		subFulfillmentType = res.subFulfillmentType,
		athleteName = "${res.athleteFirstName} ${res.athleteLastName}",
		orderNumber = res.orderNumber.orEmpty(),
		pickedBy = res.pickedBy.orEmpty(),
		packItems = res.items.flatMap { packTaskItem ->
			val workableQty = packTaskItem.qty - packTaskItem.declinedQty
			List(workableQty.coerceAtLeast(0)) { packTaskItem }
		},
		holdSlipZPL = res.holdSlipZPL
	)
}
