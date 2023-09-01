package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.RecordHoldingLocationRequest
import com.nextuple.nsf.retrofit.dto.StageTask
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PrepApi {

	@POST("v1/pack-task/start/{taskId}")
	suspend fun startPack(
		@Path("taskId") taskId: String,
		@Header("userId") userId: String
	): ApiResponse<PackTask>

	@POST("v1/pack-task/pack/{taskId}")
	suspend fun packAndGetHoldSlip(
		@Path("taskId") taskId: String,
		@Header("userId") userId: String
	): ApiResponse<StageTask>

	@POST("v1/stage-task/holding-location")
	suspend fun recordHoldingLocation(
		@Header("userId") userId: String,
		@Body recordHoldingLocationRequest: RecordHoldingLocationRequest
	): ApiResponse<StageTask>
}
