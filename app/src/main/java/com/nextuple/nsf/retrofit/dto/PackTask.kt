package com.nextuple.nsf.retrofit.dto

/**
 * TODO: Refactor away the need for this class.
 */
data class PackTask(
	val id: Long,
    val status: Status? = null,
    val fulfillmentRequestNumber: String
)


