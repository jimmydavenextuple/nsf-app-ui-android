package com.nextuple.nsf.service.dto

import com.nextuple.nsf.retrofit.dto.ApiResponse
import com.nextuple.nsf.service.dto.Result.ErrorType.GENERAL
import com.nextuple.nsf.service.dto.Result.ErrorType.NETWORK
import com.nextuple.nsf.service.dto.Result.ErrorType.NOT_FOUND

/**
 * A custom result wrapper intended to simplify call handling in the [com.nextuple.nsf.ui.state] layer
 * implementations. Implementations in [com.nextuple.nsf.service] layer should always leverage this.
 */
sealed class Result<out R> {

	/**
	 * Represents a success.
	 */
	data class Success<R>(val data: R) : Result<R>()

	/**
	 * Represents an error.
	 */
	data class Error(val msg: String? = null, val type: ErrorType = GENERAL) : Result<Nothing>()

	companion object {
		/**
		 * @param apiResponse the response wrapper to convert
		 * @param transformForSuccess the transform that will be applied for success responses
		 * @return the converted wrapper
		 */
		fun <D, R> fromApiResponse(
			apiResponse: ApiResponse<D>,
			transformForSuccess: (D?) -> R
		): Result<R> = when (apiResponse) {
			is ApiResponse.Success -> {
				Success(data = transformForSuccess(apiResponse.data))
			}

			is ApiResponse.Error -> {
				val errorType = when (apiResponse.status) {
					404 -> NOT_FOUND
					else -> GENERAL
				}
				Error(msg = apiResponse.msg, type = errorType)
			}

			is ApiResponse.NetworkError -> {
				Error(msg = apiResponse.msg, type = NETWORK)
			}
		}

		fun generalError(): Error = Error(msg = "Oops! Something went wrong.")
	}

	enum class ErrorType {
		/**
		 * A catch-all type when the specific error reason doesn't matter.
		 */
		GENERAL,

		/**
		 * Represents an error where the client is unable to connect to APIs.
		 */
		NETWORK,

		/**
		 * Represents an error where data is not found. Note that list-based
		 * results do not apply to this type in most cases.
		 */
		NOT_FOUND
	}
}
