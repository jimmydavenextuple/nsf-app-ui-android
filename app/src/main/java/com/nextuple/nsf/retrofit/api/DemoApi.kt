package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.response.DemoCreateFRResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DemoApi {

	@POST("v1/demo/bopis/{option}")
	suspend fun createFRBOPIS(
		@Path("option") option: Int,
		@Query("store") store: String,
		@Header("userId") userId: String,
	): ApiResponse<List<DemoCreateFRResponse?>>

	@POST("v1/demo/sdd/{option}")
	suspend fun createFRSDD(
		@Path("option") option: Int,
		@Query("store") store: String,
		@Header("userId") userId: String,
	): ApiResponse<List<DemoCreateFRResponse?>>
}
