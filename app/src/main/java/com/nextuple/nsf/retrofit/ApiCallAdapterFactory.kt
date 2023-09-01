package com.nextuple.nsf.retrofit

import com.nextuple.nsf.retrofit.dto.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A custom [CallAdapter.Factory] that provides [CallAdapter] instances for supported Retrofit
 * interface functions.
 *
 * To be supported, must be of the following signatures:
 *
 * ```
 * 	suspend fun getData(): ApiResponse<Data>
 * 	fun getData(): Call<ApiResponse<Data>>
 * ```
 *
 * The `suspend` form is preferred and `Data` is a made-up type for above example purposes only.
 */
class ApiCallAdapterFactory : CallAdapter.Factory() {

	override fun get(
		returnType: Type,
		annotations: Array<out Annotation>,
		retrofit: Retrofit
	): CallAdapter<*, *>? {
		// Return type should be wrapped with Call (suspend keyword abstracts that).
		if (returnType !is ParameterizedType || getRawType(returnType) != Call::class.java) {
			return null
		}

		// Wrapped type should be our custom response wrapper type.
		val resType = getParameterUpperBound(0, returnType)
		if (resType !is ParameterizedType || getRawType(resType) != ApiResponse::class.java) {
			return null
		}

		// Extract the actual data type.
		val dataType = getParameterUpperBound(0, resType)
		return ApiCallAdapter<Any>(dataType)
	}
}
