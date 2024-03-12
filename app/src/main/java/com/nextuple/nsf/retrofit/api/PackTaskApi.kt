package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.GetPrepDetailsRequest
import com.nextuple.nsf.retrofit.dto.PackByGearRequest
import com.nextuple.nsf.retrofit.dto.PrepDetail
import com.nextuple.nsf.retrofit.dto.StageTask
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PackTaskApi {

	@POST("v1/pack-task/pack/{taskId}")
	suspend fun packAndGetHoldSlip(
		@Path("taskId") taskId: String,
		@Header("userId") userId: String
	): ApiResponse<StageTask>

	@POST("v1/pack-task/getPrepDetails")
	suspend fun getPrepDetails(
		@Header("userId") userId: String,
		@Body getPrepDetailsRequest: GetPrepDetailsRequest
	): ApiResponse<List<PrepDetail>>

	@POST("v1/pack-task/packByGear")
	suspend fun packByGear(
		@Header("userId") userId: String,
		@Body packByGearRequest: PackByGearRequest
	): ApiResponse<PrepDetail>
}
