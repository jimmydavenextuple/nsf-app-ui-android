package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.GetPrepDetailsRequest
import com.nextuple.nsf.retrofit.dto.PackByGearRequest
import com.nextuple.nsf.retrofit.dto.PackByOrderRequest
import com.nextuple.nsf.retrofit.dto.PrepDetail
import com.nextuple.nsf.retrofit.dto.RecordDeclineRequest
import com.nextuple.nsf.retrofit.dto.RecordDeclineResponse
import com.nextuple.nsf.retrofit.dto.StageTask
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PackTaskApi {

	@POST("v1/pack-task/getPrepDetails")
	suspend fun getPrepDetails(
		@Header("userId") userId: String,
		@Body getPrepDetailsRequest: GetPrepDetailsRequest
	): ApiResponse<List<PrepDetail>>

	/**
	 * Begin the pack process by gear. Creates the pack task if one does not already exist.
	 */
	@POST("v1/pack-task/packByGear")
	suspend fun packByGear(
		@Header("userId") userId: String,
		@Body packByGearRequest: PackByGearRequest
	): ApiResponse<PrepDetail>

	/**
	 * Begin the pack process by order. Creates the pack task if one does not already exist.
	 */
	@POST("v1/pack-task/packByOrder")
	suspend fun packByOrder(
		@Header("userId") userId: String,
		@Body packByOrderRequest: PackByOrderRequest
	): ApiResponse<PrepDetail>

	/**
	 * Starts an existing pack task.
	 */
	@POST("v1/pack-task/start/{taskId}")
	suspend fun startPackTask(
		@Path("taskId") taskId: String,
		@Header("userId") userId: String
	): ApiResponse<PrepDetail>

	/**
	 * Completes a started pack task and then advances to stage.
	 */
	@POST("v1/pack-task/complete/{taskId}")
	suspend fun completePackAndGetHoldSlip(
		@Path("taskId") taskId: String,
		@Header("userId") userId: String
	): ApiResponse<StageTask>

	/**
	 * Starts the pack task, completes the pack task, and then advances to stage.
	 */
	@POST("v1/pack-task/pack/{taskId}")
	suspend fun packAndGetHoldSlip(
		@Path("taskId") taskId: String,
		@Header("userId") userId: String
	): ApiResponse<StageTask>

	@POST("v1/pack-task/record-decline")
	suspend fun declinePackItem(
		@Header("userId") userId: String,
		@Body recordDeclineRequest: RecordDeclineRequest
	): ApiResponse<RecordDeclineResponse>
}
