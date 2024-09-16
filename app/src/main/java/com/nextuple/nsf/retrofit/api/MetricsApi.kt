package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.response.MetricsSummaryResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MetricsApi {

	@GET("v1/metrics/summary/{store}")
	suspend fun getMetricsSummary(
		@Path("store") store: String,
		@Header("userId") userId: String
	): ApiResponse<MetricsSummaryResponse>
}
