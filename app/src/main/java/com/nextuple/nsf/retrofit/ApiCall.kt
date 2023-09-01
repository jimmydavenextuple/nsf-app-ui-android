package com.nextuple.nsf.retrofit

import com.nextuple.nsf.retrofit.dto.ApiResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A custom [Call] that handles re-routing Retrofit error paths to a single success
 * path returning [ApiResponse].
 */
internal class ApiCall<D>(
	private val delegate: Call<D>
) : Call<ApiResponse<D>> {

	override fun enqueue(callback: Callback<ApiResponse<D>>) =
		delegate.enqueue(object : Callback<D> {
			override fun onResponse(call: Call<D>, res: Response<D>) = callback.onResponse(
				this@ApiCall,
				Response.success(
					if (res.isSuccessful) {
						ApiResponse.Success(data = res.body())
					} else {
						ApiResponse.Error(msg = res.errorBody()?.string(), status = res.code())
					}
				)
			)

			override fun onFailure(call: Call<D>, t: Throwable) = callback.onResponse(
				this@ApiCall,
				Response.success(ApiResponse.NetworkError(msg = t.message))
			)
		})

	override fun execute(): Response<ApiResponse<D>> = throw UnsupportedOperationException()
	override fun isExecuted(): Boolean = delegate.isExecuted
	override fun cancel() = delegate.cancel()
	override fun isCanceled(): Boolean = delegate.isCanceled
	override fun request(): Request = delegate.request()
	override fun timeout(): Timeout = delegate.timeout()
	override fun clone(): ApiCall<D> = ApiCall(delegate.clone())
}
