package com.nextuple.nsf.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.response.DemoCreateFRResponse
import com.nextuple.nsf.service.DemoService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class DemoViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER") handler: SavedStateHandle?,
	private val demoService: DemoService?
) : ViewModel() {
	var demoApiState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var errMsg: String? by mutableStateOf(null)
		protected set

	var demoCreateFRResponse: DemoCreateFRResponse? by mutableStateOf(null)

	fun createFRBOPIS(option: Int) {
		viewModelScope.launch {
			demoApiState = GenericViewState.Loading

			demoCreateFRResponse = when (val response = demoService!!.createFRBOPIS(option)) {
				is Result.Success -> {
					errMsg = null
					demoApiState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					demoApiState = GenericViewState.Failure
					null
				}
			}
		}
	}

	fun createFRSDD(option: Int) {
		viewModelScope.launch {
			demoApiState = GenericViewState.Loading

			demoCreateFRResponse = when (val response = demoService!!.createFRSDD(option)) {
				is Result.Success -> {
					errMsg = null
					demoApiState = GenericViewState.Success
					response.data
				}

				is Result.Error -> {
					errMsg = response.msg
					demoApiState = GenericViewState.Failure
					null
				}
			}
		}
	}
}
