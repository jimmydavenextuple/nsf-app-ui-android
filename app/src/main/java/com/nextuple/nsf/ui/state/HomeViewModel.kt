package com.nextuple.nsf.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.response.MetricsSummaryResponse
import com.nextuple.nsf.service.MetricsService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val metricsService: MetricsService
) : ViewModel() {
	var metricsSummaryData: MetricsSummaryData by mutableStateOf(MetricsSummaryData())
		private set

	fun setMetricsSummary() = viewModelScope.launch {
		metricsSummaryData = MetricsSummaryData(state = GenericViewState.Loading)
		metricsSummaryData = when (val res = metricsService.getMetricsSummary()) {
			is Result.Success -> {
				MetricsSummaryData(state = GenericViewState.Success, data = res.data)
			}

			is Result.Error -> {
				MetricsSummaryData(state = GenericViewState.Failure)
			}
		}
	}
}

data class MetricsSummaryData(
	val state: GenericViewState = GenericViewState.Idle,
	val data: MetricsSummaryResponse? = null
)
