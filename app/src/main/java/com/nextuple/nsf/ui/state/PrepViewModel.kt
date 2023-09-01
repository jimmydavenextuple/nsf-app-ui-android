package com.nextuple.nsf.ui.state

import androidx.compose.runtime.State
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
import com.nextuple.nsf.service.PrepService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrepViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle,
	private val prepService: PrepService
) : ViewModel() {

	var startPackState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var holdSlipState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var holdLocationState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var storeOverview: StoreOverviewResponse? by mutableStateOf(null)
	var packTask: PackTask? by mutableStateOf(null)
		private set

	var stageTask: StageTask? by mutableStateOf(null)
		private set

	var athlete: State<String?> = derivedStateOf {
		"${packTask?.athleteFirstName} ${packTask?.athleteLastName}"
	}

	var orderNum: State<String?> = derivedStateOf {
		packTask?.orderNumber
	}

	var packItems: State<List<PackTaskItem>?> = derivedStateOf {
		packTask?.items
	}

	var errMsg: String? by mutableStateOf(null)
		private set

	var currentPreStage = derivedStateOf {
		if (storeOverview?.userOverview?.currentStageTask != null) {
			AppViewModel.PrepStage.Stage2
		} else if (storeOverview?.userOverview?.currentPackTask != null) {
			AppViewModel.PrepStage.Stage1
		} else {
			AppViewModel.PrepStage.Landing
		}
	}

	fun onStoreOverViewCompletion(storeOverviewResponse: StoreOverviewResponse?) {
		storeOverview = storeOverviewResponse
		packTask = storeOverview?.userOverview?.currentPackTask
		stageTask = storeOverview?.userOverview?.currentStageTask
	}

	fun startPack(taskId: String, dks: String) {
		viewModelScope.launch {
			startPackState = GenericViewState.Loading
			packTask = when (val response = prepService.startPack(taskId = taskId, dks = dks)) {
				is Result.Success -> {
					errMsg = null
					startPackState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					if (response.type == Result.ErrorType.NOT_FOUND) {
						startPackState = GenericViewState.Failure
					} else {
						resetPackState()
						/*
							TODO: Display Toast Error Message From View
						 */
					}
					null
				}
			}
		}
	}

	fun packAndGetHoldSlip(dks: String) {
		holdSlipState = GenericViewState.Loading
		viewModelScope.launch {
			stageTask = when (
				val response = prepService.packAndGetHoldSlip(
					taskId = packTask?.id?.toString() ?: "",
					dks = dks
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

	fun recordHoldingLocation(containerId: Long, holdingLocation: String, dks: String) {
		viewModelScope.launch {
			holdLocationState = GenericViewState.Loading
			val recordHoldingLocationRequest = RecordHoldingLocationRequest(
				containerId = containerId,
				holdingLocation = holdingLocation
			)
			stageTask = when (
				val response = prepService.recordHoldingLocation(recordHoldingLocationRequest, dks)
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

	fun resetPackState() {
		startPackState = GenericViewState.Idle
	}

	fun resetHoldSlipState() {
		holdSlipState = GenericViewState.Idle
	}

	fun resetHoldLocationState() {
		holdLocationState = GenericViewState.Idle
	}
}
