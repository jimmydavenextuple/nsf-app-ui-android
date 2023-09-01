package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AppApi {

	@GET("v1/overview/store/{store}")
	suspend fun getStoreOverview(
		@Path("store") store: String,
		@Header("userId") userId: String
	): ApiResponse<StoreOverviewResponse>
}
