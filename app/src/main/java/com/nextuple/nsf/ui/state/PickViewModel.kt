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
import com.nextuple.nsf.util.FulfillmentType
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

	var sfsPickTask: PickTask? by mutableStateOf(null)
		private set

	var sfsTasks: Int by mutableStateOf(0)
		private set

	var currentPickItem: PickTaskItem? by mutableStateOf(null)
		private set

	var showSubstitutionModal = MutableLiveData<Boolean>(false)

	fun updateCurrentPickItem() {
		val currentTask = pickTask ?: sfsPickTask
		currentPickItem = currentTask?.items?.find {
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
		
		// Fetch PICKUP tasks
		when (val res = infoService?.getUserPickTasks()) {
			is Result.Success -> {
				val task = res.data?.pickTask
				if (task?.fulfillmentType == FulfillmentType.BOPIS || task?.fulfillmentType == FulfillmentType.SAME_DAY) {
					pickTask = task
				} else {
					pickTask = null
				}
			}
			is Result.Error -> {
				pickTask = null
			}
			null -> TODO()
		}
		
		// Fetch SFS tasks
		when (val sfsRes = infoService?.getUserSfsPickTasks()) {
			is Result.Success -> {
				sfsPickTask = sfsRes.data?.pickTask
				sfsTasks = if (sfsPickTask != null) 1 else 0
			}
			is Result.Error -> {
				sfsPickTask = null
				sfsTasks = 0
			}
			null -> TODO()
		}
		
		viewState = GenericViewState.Success
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

	fun startSfsPick() {
		startPickState = GenericViewState.Loading
		viewModelScope.launch {
			sfsPickTask = when (val response = pickService?.startPick()) {
				is Result.Success -> {
					if (response.data?.fulfillmentType == FulfillmentType.SFS) {
						startPickState = GenericViewState.Success
						response.data
					} else {
						startPickState = GenericViewState.Failure
						null
					}
				}
				is Result.Error -> {
					if (response.type == NOT_FOUND) {
						startPickState = GenericViewState.Failure
					} else {
						resetPickScreen()
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
			val currentTask = pickTask ?: sfsPickTask
			val declineItemRequest = DeclineItemRequest(
				taskId = currentTask?.id ?: 0,
				sku = currentPickItem?.originalItem?.sku ?: currentPickItem?.sku ?: "",
				declinedQty = getDeclineQty(),
				declineReason = currentPickItem?.originalItemDeclineReason ?: declineReason,
				declineReasonText = currentPickItem?.originalItemDeclineReasonText ?: declineReasonText
			)
			val response = pickService?.declinePick(req = declineItemRequest)
			when (response) {
				is Result.Success -> {
					pickDeclineState = GenericViewState.Success
					if (pickTask != null) {
						pickTask = response.data
					} else {
						sfsPickTask = response.data
					}
				}

				is Result.Error -> {
					pickDeclineState = GenericViewState.Failure
				}

				null -> TODO()
			}
		}
		}
	}

	private fun setOriginalItemDeclineReason(declineReason: String, declineReasonText: String) {
		val currentTask = pickTask ?: sfsPickTask
		val currentItemIdx = currentTask?.items?.indexOf(currentPickItem)
		// create a copy of (pick task) items
		val updatedItems = (currentTask?.items?.toMutableList() ?: emptyList()).toMutableList()
		updatedItems[currentItemIdx!!].originalItemDeclineReason = declineReason
		updatedItems[currentItemIdx].originalItemDeclineReasonText = declineReasonText
		// update (pick task) items
		if (pickTask != null) {
			pickTask?.items = updatedItems
		} else {
			sfsPickTask?.items = updatedItems
		}
	}

	fun onSubstitutionSelected(sku: String) {
		val currentTask = pickTask ?: sfsPickTask
		val currentItemIdx = currentTask?.items?.indexOf(currentPickItem)
		val currentItemSubstitutions = (currentPickItem?.substitutions ?: emptyList()).toMutableList()

		// create a copy of (pick task) items
		val updatedItems = (currentTask?.items?.toMutableList() ?: emptyList()).toMutableList()
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
		if (pickTask != null) {
			pickTask?.items = updatedItems
		} else {
			sfsPickTask?.items = updatedItems
		}
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

		val currentTask = pickTask ?: sfsPickTask
		if (currentTask?.id == null) {
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
				taskId = currentTask.id ?: 0,
				sku = currentPickItem?.originalItem?.sku ?: currentPickItem?.sku ?: "",
				substitutedSku = if (currentPickItem?.originalItem != null) currentPickItem?.sku else null,
				scannedUpc = upc,
				pickedQty = 1,
				pickedLocation = pickLocation
			)
			val res = pickService?.pickItem(req = pickItemRequest)
			when (res) {
				is Result.Success -> {
					recordPickState = GenericViewState.Success
					if (pickTask != null) {
						pickTask = res.data
					} else {
						sfsPickTask = res.data
					}
				}

				is Result.Error -> {
					recordPickState = GenericViewState.Failure
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
		sfsPickTask = null
		viewState = GenericViewState.Success
	}

	private fun getDeclineQty(): Int = currentPickItem?.let {
		it.qty - it.pickedQty - it.declinedQty
	} ?: 0
}
