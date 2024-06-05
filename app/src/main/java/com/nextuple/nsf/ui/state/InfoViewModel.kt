package com.nextuple.nsf.ui.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.InfoService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class InfoViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER") handler: SavedStateHandle,
	private val infoService: InfoService
) : ViewModel() {
	var storeOverviewState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var errMsg: String? by mutableStateOf(null)
		protected set

	var declineCodes: GetDeclineCodesResponse? by mutableStateOf(null)
		private set

	var declineCodesState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	val pickTasksUnassigned by derivedStateOf {
		storeOverview?.pickOverview?.tasksUnassigned ?: 0
	}

	val prepTasksUnassigned by derivedStateOf {
		storeOverview?.prepOverview?.tasksUnassigned ?: 0
	}

	val totalStoreUnitsInProgress by derivedStateOf {
		storeOverview?.pickOverview?.unitsInProgress ?: 0
	}

	val totalStoreUnitsWorked by derivedStateOf {
		storeOverview?.pickOverview?.unitsWorked ?: 0
	}

	val totalStoreUnits by derivedStateOf {
		storeOverview?.pickOverview?.run {
			unitsUnassigned + unitsWorked + unitsInProgress
		} ?: 0
	}

	private var storeOverview: StoreOverviewResponse? by mutableStateOf(null)

	fun getStoreOverview() {
		viewModelScope.launch {
			storeOverviewState = GenericViewState.Loading
			storeOverview = when (val response = infoService.getStoreOverview()) {
				is Result.Success -> {
					errMsg = null
					storeOverviewState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					storeOverviewState = GenericViewState.Failure
					null
				}
			}
		}
	}

	fun getDeclineCodes() {
		if (declineCodes?.pickDeclineCodes.isNullOrEmpty()) {
			declineCodesState = GenericViewState.Loading
			viewModelScope.launch {
				declineCodes = when (val response = infoService.getDeclineCodes()) {
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

	fun onLogout() {
		declineCodes = null
		storeOverview = null
	}
}
