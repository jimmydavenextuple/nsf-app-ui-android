package com.nextuple.nsf.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
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
	@Suppress("UNUSED_PARAMETER") handler: SavedStateHandle?,
	private val infoService: InfoService?,
	private val pickService: PickService?
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

	var currentPickItem: PickTaskItem? by mutableStateOf(null)
		private set

	var showSubstitutionModal = MutableLiveData<Boolean>(false)

	fun updateCurrentPickItem() {
		currentPickItem = pickTask?.items?.find {
			it.pickedQty.plus(it.declinedQty) != it.qty
		}
	}

	fun toggleShowSubstitutionModal() {
		showSubstitutionModal.value = !showSubstitutionModal.value!!
	}

	fun resetPickScreen() {
		startPickState = GenericViewState.Idle
	}

	fun fetchCurrentStep() = viewModelScope.launch {
		viewState = GenericViewState.Loading
		when (val res = infoService?.getUserPickTasks()) {
			is Result.Success -> {
				pickTask = res.data?.pickTask
				viewState = GenericViewState.Success
			}

			is Result.Error -> {
				pickTask = null
				viewState = GenericViewState.Failure
			}

			null -> TODO()
		}
	}

	fun startPick() {
		startPickState = GenericViewState.Loading
		viewModelScope.launch {
			pickTask = when (val response = pickService?.startPick()) {
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

				null -> TODO()
			}
		}
	}

	fun declinePick(declineReason: String, declineReasonText: String) {
		if (currentPickItem?.substitutionAllowed == true && !currentPickItem?.substitutions.isNullOrEmpty()) {
			if (currentPickItem?.originalItem == null) {
				setOriginalItemDeclineReason(declineReason, declineReasonText)
			}
			showSubstitutionModal.value = true
		} else {
		pickDeclineState = GenericViewState.Loading
		viewModelScope.launch {
			val declineItemRequest = DeclineItemRequest(
				taskId = pickTask?.id ?: 0,
				sku = currentPickItem?.originalItem?.sku ?: currentPickItem?.sku ?: "",
				declinedQty = getDeclineQty(),
				declineReason = currentPickItem?.originalItemDeclineReason ?: declineReason,
				declineReasonText = currentPickItem?.originalItemDeclineReasonText ?: declineReasonText
			)
			pickTask =
				when (val response = pickService?.declinePick(req = declineItemRequest)) {
					is Result.Success -> {
						pickDeclineState = GenericViewState.Success
						response.data
					}

					is Result.Error -> {
						pickDeclineState = GenericViewState.Failure
						null
					}

					null -> TODO()
				}
			}
		}
	}

	private fun setOriginalItemDeclineReason(declineReason: String, declineReasonText: String) {
		val currentItemIdx = pickTask?.items?.indexOf(currentPickItem)
		// create a copy of (pick task) items
		val updatedItems = (pickTask?.items?.toMutableList() ?: emptyList()).toMutableList()
		updatedItems[currentItemIdx!!].originalItemDeclineReason = declineReason
		updatedItems[currentItemIdx].originalItemDeclineReasonText = declineReasonText
		// update (pick task) items
		pickTask?.items = updatedItems
	}

	fun onSubstitutionSelected(sku: String) {
		val currentItemIdx = pickTask?.items?.indexOf(currentPickItem)
		val currentItemSubstitutions = (currentPickItem?.substitutions ?: emptyList()).toMutableList()

		// create a copy of (pick task) items
		val updatedItems = (pickTask?.items?.toMutableList() ?: emptyList()).toMutableList()
		// update current item to selected substitution item
		val selectedSubstitution = currentItemSubstitutions.find { it.sku == sku }!!
		updatedItems[currentItemIdx!!] = selectedSubstitution
		// remove selected substitution item from substitutions
		currentItemSubstitutions.removeAt(currentItemSubstitutions.indexOf(selectedSubstitution))

		if (currentItemSubstitutions.isNotEmpty()) {
			// substitutions isNotEmpty
			// set substitutionAllowed and substitutions for current item
			updatedItems[currentItemIdx].substitutionAllowed = true
			updatedItems[currentItemIdx].substitutions = currentItemSubstitutions
		}

		if (currentPickItem?.originalItem != null) {
			updatedItems[currentItemIdx].originalItem = currentPickItem?.originalItem
		} else {
			updatedItems[currentItemIdx].originalItem = currentPickItem
		}

		// update (pick task) items
		pickTask?.items = updatedItems
		// update current (pick) item
		updateCurrentPickItem()
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
				sku = currentPickItem?.originalItem?.sku ?: currentPickItem?.sku ?: "",
				substitutedSku = if (currentPickItem?.originalItem != null) currentPickItem?.sku else null,
				scannedUpc = upc,
				pickedQty = 1,
				pickedLocation = pickLocation
			)
			pickTask = when (
				val res = pickService?.pickItem(req = pickItemRequest)
			) {
				is Result.Success -> {
					recordPickState = GenericViewState.Success
					res.data
				}

				is Result.Error -> {
					recordPickState = GenericViewState.Failure
					null
				}

				null -> TODO()
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
