package com.nextuple.nsf.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.AppService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class AppViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER") handler: SavedStateHandle,
	private val appService: AppService
) : ViewModel() {
	var storeOverviewState: GenericViewState by mutableStateOf(GenericViewState.Loading)
		private set

	var errMsg: String? by mutableStateOf(null)
		protected set

	private var storeOverview: StoreOverviewResponse? by mutableStateOf(null)

	fun getStoreOverview(dks: String, onStoreOverviewCompletion: (GenericViewState, StoreOverviewResponse?) -> Unit) {
		viewModelScope.launch {
			if (dks.isNullOrEmpty()) {
				errMsg = "Invalid DKS number."
				storeOverviewState = GenericViewState.Failure
				return@launch
			}
			storeOverviewState = GenericViewState.Loading
			storeOverview = when (val response = appService.getStoreOverview(dks = dks)) {
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
			onStoreOverviewCompletion(storeOverviewState, storeOverview)
		}
	}

	sealed class PrepStage() {
		object Landing : PrepStage()
		object Stage1 : PrepStage()
		object Stage2 : PrepStage()
	}
}
