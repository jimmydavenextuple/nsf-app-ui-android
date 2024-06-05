package com.nextuple.nsf.ui.state

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
import com.nextuple.nsf.service.InfoService
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
	private val infoService: InfoService,
	private val pickService: PickService
) : ViewModel() {

	var viewState: GenericViewState by mutableStateOf(GenericViewState.Loading)
		private set
	var startPickState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var pickDeclineState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var recordPickState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var pickTask: PickTask? by mutableStateOf(null)
		private set

	val currentPickItem: PickTaskItem? by derivedStateOf {
		pickTask?.items?.find {
			it.pickedQty.plus(it.declinedQty) != it.qty
		}
	}

	fun resetPickScreen() {
		startPickState = GenericViewState.Idle
	}

	fun fetchCurrentStep() = viewModelScope.launch {
		viewState = GenericViewState.Loading
		when (val res = infoService.getUserPickTasks()) {
			is Result.Success -> {
				pickTask = res.data?.pickTask
				viewState = GenericViewState.Success
			}

			is Result.Error -> {
				pickTask = null
				viewState = GenericViewState.Failure
			}
		}
	}

	fun startPick() {
		startPickState = GenericViewState.Loading
		viewModelScope.launch {
			pickTask = when (val response = pickService.startPick()) {
				is Result.Success -> {
					startPickState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
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
				sku = currentPickItem?.sku ?: "",
				declinedQty = getDeclineQty(),
				declineReason = declineReason,
				declineReasonText = declineReasonText
			)
			pickTask =
				when (val response = pickService.declinePick(req = declineItemRequest)) {
					is Result.Success -> {
						pickDeclineState = GenericViewState.Success
						response.data
					}

					is Result.Error -> {
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
			recordPickState = GenericViewState.Failure
			return false
		}

		if (pickTask?.id == null) {
			recordPickState = GenericViewState.Failure
			return false
		}
		if (currentPickItem?.sku.isNullOrEmpty()) {
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
				sku = currentPickItem?.sku ?: "",
				scannedUpc = upc,
				pickedQty = 1,
				pickedLocation = pickLocation
			)
			pickTask = when (
				val res = pickService.pickItem(req = pickItemRequest)
			) {
				is Result.Success -> {
					recordPickState = GenericViewState.Success
					res.data
				}

				is Result.Error -> {
					recordPickState = GenericViewState.Failure
					null
				}
			}
		}
		return true
	}

	fun matchUpc(upc: String?): Boolean =
		upc != null && currentPickItem?.upcs.orEmpty().contains(upc)

	fun resetPickDeclineState() {
		pickDeclineState = GenericViewState.Idle
	}

	fun resetRecordPickState() {
		recordPickState = GenericViewState.Idle
	}

	fun resetPickState() {
		pickTask = null
		viewState = GenericViewState.Success
	}

	private fun getDeclineQty(): Int = currentPickItem?.let {
		it.qty - it.pickedQty - it.declinedQty
	} ?: 0
}
