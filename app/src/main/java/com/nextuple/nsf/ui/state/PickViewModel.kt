package com.nextuple.nsf.ui.state

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nextuple.nsf.service.dto.Result
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.DeclineItemRequest
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.retrofit.dto.PickTaskItem
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.PickService
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

	var startPickState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var pickDeclineState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var recordPickState: GenericViewState by mutableStateOf(GenericViewState.Idle)
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

	fun onStoreOverview(res: StoreOverviewResponse?) {
		pickTask = res?.userOverview?.currentPickTask
	}

	fun resetPickScreen() {
		startPickState = GenericViewState.Idle
	}

	fun startPick() {
		startPickState = GenericViewState.Loading
		viewModelScope.launch {
			pickTask = when (val response = pickService.startPick()) {
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

	fun declinePick(declineReason: String, declineReasonText: String) {
		pickDeclineState = GenericViewState.Loading
		viewModelScope.launch {
			val declineItemRequest = DeclineItemRequest(
				taskId = pickTask?.id ?: 0,
				sku = currentPickItem.value?.sku ?: "",
				declinedQty = getDeclineQty(),
				declineReason = declineReason,
				declineReasonText = declineReasonText
			)
			pickTask =
				when (val response = pickService.declinePick(req = declineItemRequest)) {
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

	/**
	 * @return true if UPC is a match, false if not a match
	 */
	fun pickItem(upc: String?, pickLocation: String?): Boolean {
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
		if (!matchUpc(upc)) {
			return false
		}

		viewModelScope.launch {
			recordPickState = GenericViewState.Loading
			val pickItemRequest = PickItemRequest(
				taskId = pickTask?.id ?: 0,
				sku = currentPickItem.value?.sku ?: "",
				scannedUpc = upc,
				pickedQty = 1,
				pickedLocation = pickLocation
			)
			pickTask = when (
				val res = pickService.pickItem(req = pickItemRequest)
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

	fun matchUpc(upc: String?): Boolean =
		upc != null && currentPickItem.value?.upcs.orEmpty().contains(upc)

	fun resetPickDeclineState() {
		pickDeclineState = GenericViewState.Idle
	}

	fun resetRecordPickState() {
		recordPickState = GenericViewState.Idle
	}

	fun onLogout() {
		pickTask = null
	}

	private fun getDeclineQty(): Int = currentPickItem.value?.let {
		it.qty - it.pickedQty - it.declinedQty
	} ?: 0
}
