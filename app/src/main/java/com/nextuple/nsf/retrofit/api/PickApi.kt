package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.DeclineItemRequest
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PickApi {

	@POST("v1/pick-task/start/{store}")
	suspend fun startPick(
		@Path("store") store: String,
		@Header("userId") userId: String
	): ApiResponse<PickTask>

	@POST("v1/pick-task/decline")
	suspend fun pickDecline(
		@Header("userId") userId: String,
		@Body declineItemRequest: DeclineItemRequest
	): ApiResponse<PickTask>

	@POST("v1/pick-task/pick")
	suspend fun recordPick(
		@Header("userId") userId: String,
		@Body pickItemRequest: PickItemRequest
	): ApiResponse<PickTask>

	@GET("v1/overview/store/{store}")
	suspend fun getStoreOverview(
		@Path("store") store: String,
		@Header("userId") userId: String
	): ApiResponse<StoreOverviewResponse>

	@GET("v1/config/decline-codes")
	suspend fun getDeclineCodes(
		@Header("userId") userId: String,
		@Query("brand") brand: String,
		@Query("store") store: String,
		@Query("fulfillmentType") fulfillmentType: String,
		@Query("subFulfillmentType") subFulfillmentType: String
	): ApiResponse<GetDeclineCodesResponse>
}
