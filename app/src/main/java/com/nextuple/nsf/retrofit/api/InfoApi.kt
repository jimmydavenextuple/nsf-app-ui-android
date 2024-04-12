package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.retrofit.dto.response.GetUserPrepTasksResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface InfoApi {

	@GET("v1/overview/store/{store}")
	suspend fun getStoreOverview(
		@Path("store") store: String,
		@Header("userId") userId: String
	): ApiResponse<StoreOverviewResponse>

	@GET("v1/tasks/prep/store/{store}")
	suspend fun getUserPrepTasks(
		@Path("store") store: String,
		@Header("userId") userId: String
	): ApiResponse<GetUserPrepTasksResponse>
	@GET("v1/config/decline-codes")
	suspend fun getDeclineCodes(
		@Header("userId") userId: String,
		@Query("brand") brand: String,
		@Query("store") store: String,
		@Query("fulfillmentType") fulfillmentType: String,
		@Query("subFulfillmentType") subFulfillmentType: String
	): ApiResponse<GetDeclineCodesResponse>
}
