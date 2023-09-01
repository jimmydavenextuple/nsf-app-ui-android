package com.nextuple.nsf.retrofit

import com.nextuple.nsf.retrofit.dto.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * A custom [CallAdapter] that wraps the data type wrapped by [Call] with [ApiResponse].
 */
class ApiCallAdapter<D>(
	private val dataType: Type
) : CallAdapter<D, Call<ApiResponse<D>>> {
	override fun responseType(): Type = dataType
	override fun adapt(call: Call<D>): Call<ApiResponse<D>> = ApiCall(call)
}
