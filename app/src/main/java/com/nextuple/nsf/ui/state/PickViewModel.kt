package com.nextuple.nsf.ui.state

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.DeclineItemRequest
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.retrofit.dto.PickTaskItem
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.PickService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Result.ErrorType.NOT_FOUND
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class PickViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER") handler: SavedStateHandle,
	private val pickService: PickService
) : ViewModel() {

	var storeOverviewState: GenericViewState by mutableStateOf(GenericViewState.Loading)
		private set

	var startPickState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var pickDeclineState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var recordPickState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var declineCodesState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var errMsg: String? by mutableStateOf(null)
		protected set

	var pickTask: PickTask? by mutableStateOf(null)
		private set

	var currentPickItem: State<PickTaskItem?> = derivedStateOf {
		pickTask?.items?.find {
			it.pickedQty.plus(it.declinedQty) != it.qty
		}
	}
	var storeOverview: StoreOverviewResponse? by mutableStateOf(null)
		protected set

	var pickTasksUnassigned = derivedStateOf {
		storeOverview?.pickOverview?.tasksUnassigned ?: 0
	}

	var prepTasksUnassigned = derivedStateOf {
		storeOverview?.prepOverview?.tasksUnassigned ?: 0
	}

	var declineCodes: GetDeclineCodesResponse? by mutableStateOf(null)
		private set

	var totalStoreUnits = derivedStateOf {
		storeOverview?.pickOverview?.run {
			unitsUnassigned + unitsWorked + unitsInProgress
		} ?: 0
	}

	var currentPickTask = derivedStateOf {
		storeOverview?.userOverview?.currentPickTask
	}

	fun resetPickScreen() {
		startPickState = GenericViewState.Idle
		storeOverviewState = GenericViewState.Success
	}

	fun startPick(dks: String) {
		storeOverviewState = GenericViewState.Idle
		startPickState = GenericViewState.Loading
		viewModelScope.launch {
			pickTask = when (val response = pickService.startPick(dks = dks)) {
				is Result.Success -> {
					errMsg = null
					startPickState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					if (response.type == NOT_FOUND) {
						startPickState = GenericViewState.Failure
					} else {
						resetPickScreen()
						/*
							TODO: Display Toast Error Message From View
						 */
					}
					null
				}
			}
		}
	}

	fun declinePick(declineReason: String, dks: String) {
		pickDeclineState = GenericViewState.Loading
		viewModelScope.launch {
			val declineItemRequest = DeclineItemRequest(
				taskId = pickTask?.id ?: 0,
				sku = currentPickItem.value?.sku ?: "",
				declinedQty = currentPickItem.value?.qty ?: 0,
				declineReason = declineReason
			)
			pickTask = when (val response = pickService.declinePick(declineItemRequest = declineItemRequest, dks = dks)) {
				is Result.Success -> {
					errMsg = null
					pickDeclineState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					pickDeclineState = GenericViewState.Failure
					null
				}
			}
		}
	}

	fun onLogout() {
		declineCodes = null
	}

	fun getDeclineCodes(dks: String) {
		if (declineCodes?.pickDeclineCodes.isNullOrEmpty()) {
			declineCodesState = GenericViewState.Loading
			viewModelScope.launch {
				declineCodes = when (val response = pickService.getDeclineCodes(dks = dks)) {
					is Result.Success -> {
						errMsg = null
						declineCodesState = GenericViewState.Success
						response.data
					}

					is Result.Error -> {
						errMsg = response.msg
						declineCodesState = GenericViewState.Failure
						null
					}
				}
			}
		}
	}

	/**
	 * @return true if UPC is a match, false if not a match
	 */
	fun pickItem(upc: String?, dks: String): Boolean {
		if (upc.isNullOrEmpty()) {
			errMsg = "Upc should not be Null or Blank"
			recordPickState = GenericViewState.Failure
			return false
		}
		if (pickTask?.id == null) {
			errMsg = "task should not be Null"
			recordPickState = GenericViewState.Failure
			return false
		}
		if (currentPickItem.value?.sku.isNullOrEmpty()) {
			errMsg = "Sku should not be Null or Blank"
			recordPickState = GenericViewState.Failure
			return false
		}
		if (matchUpc(upc) == null) {
			return false
		}

		viewModelScope.launch {
			recordPickState = GenericViewState.Loading
			val pickItemRequest = PickItemRequest(
				taskId = pickTask?.id ?: 0,
				sku = currentPickItem.value?.sku ?: "",
				scannedUpc = upc,
				pickedQty = currentPickItem.value?.qty ?: 0,
				pickedLocation = currentPickItem.value?.locations?.firstOrNull().orEmpty()
			)
			pickTask = when (
				val res = pickService.pickItem(pickItemRequest = pickItemRequest, dks = dks)
			) {
				is Result.Success -> {
					errMsg = null
					recordPickState = GenericViewState.Success
					res.data
				}

				is Result.Error -> {
					errMsg = res.msg
					recordPickState = GenericViewState.Failure
					null
				}
			}
		}
		return true
	}

	fun onStoreOverViewCompletion(storeOverviewState: GenericViewState, storeOverviewResponse: StoreOverviewResponse?) {
		storeOverview = storeOverviewResponse
		pickTask = storeOverview?.userOverview?.currentPickTask
		this.storeOverviewState = storeOverviewState
	}

	private fun matchUpc(upc: String?): String? {
		upc ?: return null
		if (currentPickItem.value?.upcs?.contains(upc) == true) {
			return upc
		}
		return null
	}

	fun resetStartPickState() {
		startPickState = GenericViewState.Idle
		storeOverview = storeOverview?.let {
			it.copy(
				pickOverview = it.pickOverview.copy(
					tasksUnassigned = (it.pickOverview.tasksUnassigned).minus(1),
					tasksInProgress = (it.pickOverview.unitsInProgress).plus(1),
					unitsUnassigned = (it.pickOverview.unitsUnassigned).minus(pickTask?.totalQty ?: 0),
					unitsInProgress = (it.pickOverview.unitsInProgress).plus(pickTask?.totalQty ?: 0)
				)
			)
		}
	}

	fun resetPickDeclineState() {
		pickDeclineState = GenericViewState.Idle
		clearCurrentActivePickTask()
	}

	fun resetRecordPickState() {
		recordPickState = GenericViewState.Idle
		clearCurrentActivePickTask()
	}

	private fun clearCurrentActivePickTask() {
		storeOverview = storeOverview?.let {
			it.copy(
				userOverview = it.userOverview.copy(
					currentPickTask = null
				)
			)
		}
	}
}
