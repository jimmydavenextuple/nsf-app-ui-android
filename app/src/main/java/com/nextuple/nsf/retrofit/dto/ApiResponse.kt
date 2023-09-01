package com.nextuple.nsf.retrofit.dto

/**
 * A custom response wrapper intended to simplify call handling in the [com.nextuple.nsf.service] layer
 * implementations. Interfaces in [com.nextuple.nsf.retrofit] should always leverage this.
 */
sealed class ApiResponse<out D>(open val status: Int) {

	/**
	 * A successful response, generally HTTP status 2xx.
	 */
	data class Success<D>(
		val data: D? = null,
		override val status: Int = 200
	) : ApiResponse<D>(status)

	/**
	 * An error from the server, generally HTTP status 4xx or 5xx.
	 */
	data class Error(
		val msg: String? = null,
		override val status: Int
	) : ApiResponse<Nothing>(status)

	/**
	 * A network error, meaning the client was unable to connect to the server.
	 * Generally, this would be HTTP status 503.
	 */
	data class NetworkError(
		val msg: String? = "Unable to connect.",
		override val status: Int = 503
	) : ApiResponse<Nothing>(status)
}
