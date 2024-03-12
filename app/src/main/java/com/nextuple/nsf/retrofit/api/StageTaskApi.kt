package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.RecordHoldingLocationRequest
import com.nextuple.nsf.retrofit.dto.StageTask
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface StageTaskApi {

	@GET("v1/stage-task/hold-slip/{fulfillmentRequestNumber}")
	suspend fun getHoldSlip(
		@Path("fulfillmentRequestNumber") fulfillmentRequestNumber: String,
		@Header("userId") userId: String
	): ApiResponse<StageTask>

	@POST("v1/stage-task/holding-location")
	suspend fun recordHoldingLocation(
		@Header("userId") userId: String,
		@Body recordHoldingLocationRequest: RecordHoldingLocationRequest
	): ApiResponse<StageTask>
}
