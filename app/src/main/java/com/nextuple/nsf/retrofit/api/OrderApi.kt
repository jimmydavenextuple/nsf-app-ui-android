package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.RecordDeclineRequest
import com.nextuple.nsf.retrofit.dto.RecordDeclineResponse
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {

	@GET("v1/orders")
	suspend fun getOrders(
		@Query("store") store: String,
		@Query("query") query: String?,
		@Query("orderTypeFilter") orderTypeFilter: List<String>? = null,
		@Query("orderStatusFilter") orderStatusFilter: List<String>? = null,
		@Query("minOrderStatus") minOrderStatus: String? = null,
		@Query("pastDays") pastDays: String? = null,
		@Header("userId") userId: String
	): ApiResponse<List<OrderDetailsResponse>>

	@GET("v1/orders/{fulfillmentRequestNumber}")
	suspend fun orderDetails(
		@Path("fulfillmentRequestNumber") fulfillmentRequestNumber: String,
		@Header("userId") userId: String
	): ApiResponse<OrderDetailsResponse>

	@POST("v1/orders/record-decline")
	suspend fun recordDecline(
		@Body recordDeclineRequest: RecordDeclineRequest,
		@Header("userId") userId: String
	): ApiResponse<RecordDeclineResponse>

	@POST("v1/pickup-task/extend/{fulfillmentRequestNumber}")
	suspend fun pickupExtend(
		@Path("fulfillmentRequestNumber") fulfillmentRequestNumber: String,
		@Header("userId") userId: String
	): ApiResponse<OrderDetailsResponse>

	@POST("v1/pickup-task/remove-check-in/{taskId}")
	suspend fun pickupRemoveCheckIn(
		@Path("taskId") taskId: String,
		@Header("userId") userId: String
	): ApiResponse<OrderDetailsResponse>

	@POST("v1/pickup-task/start/{fulfillmentRequestNumber}")
	suspend fun startPickupTask(
		@Path("fulfillmentRequestNumber") fulfillmentRequestNumber: String,
		@Header("userId") userId: String
	): ApiResponse<OrderDetailsResponse>

	@POST("v1/pickup-task/complete/{taskId}")
	suspend fun completePickupTask(
		@Path("taskId") taskId: String,
		@Header("userId") userId: String
	): ApiResponse<OrderDetailsResponse>
}
