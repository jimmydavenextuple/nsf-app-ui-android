package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.retrofit.dto.LoginData
import com.nextuple.nsf.retrofit.dto.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

	@POST("v1/user/login")
	suspend fun login(@Body req: LoginRequest): ApiResponse<LoginData>
}
