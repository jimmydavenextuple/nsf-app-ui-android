package com.nextuple.nsf.ui.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrepViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle
) : ViewModel() {

	var packTask: PackTask? by mutableStateOf(null)
		private set

	var stageTask: StageTask? by mutableStateOf(null)
		private set

	val currentPrepStage = derivedStateOf {
		when {
			stageTask != null && stageTask?.status?.name == "COMPLETED" -> InfoViewModel.PrepStage.Landing
			stageTask != null && stageTask?.status?.name != "COMPLETED" -> InfoViewModel.PrepStage.StageOrder
			packTask != null -> InfoViewModel.PrepStage.PrepOrder
			else -> InfoViewModel.PrepStage.Landing
		}
	}

	fun onStoreOverview(res: StoreOverviewResponse?) {
		packTask = res?.userOverview?.currentPackTask
		stageTask = res?.userOverview?.currentStageTask
	}

	fun resetPrepStage() {
		stageTask = null
	}

	fun onLogout() {
		stageTask = null
		packTask = null
	}
}
