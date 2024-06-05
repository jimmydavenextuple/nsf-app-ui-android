package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.MetricsApi
import com.nextuple.nsf.retrofit.dto.response.MetricsSummaryResponse
import com.nextuple.nsf.service.dto.Result
import javax.inject.Inject

class MetricsService @Inject constructor(
	private val metricsApi: MetricsApi,
	private val deviceService: DeviceService,
	private val userRepository: UserRepository,
	private val logService: LogService
) {
	suspend fun getMetricsSummary(): Result<MetricsSummaryResponse> {
		val store = deviceService.getStore() ?: return Result.generalError()
		val dks = userRepository.getDks() ?: return Result.generalError()

		val res = metricsApi.getMetricsSummary(store.id, userId = dks)

		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			logService.trackError(
				attemptedAction = "getMetricsSummary",
				t = it,
				additionalProps = mapOf(
					"store" to store.id
				)
			)
		}.getOrDefault(Result.generalError())
	}
}
