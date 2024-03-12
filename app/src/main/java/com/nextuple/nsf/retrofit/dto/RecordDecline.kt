package com.nextuple.nsf.retrofit.dto

data class RecordDeclineRequest(
	val fulfillmentRequestNumber: String,
	val sku: String? = null,
	val declinedUnits: Int? = null,
	val declinedReason: String,
	val shouldTranslateReason: Boolean = false,
	val action: String
)

data class RecordDeclineResponse(
	val success: Boolean?,
	val message: String?
)
