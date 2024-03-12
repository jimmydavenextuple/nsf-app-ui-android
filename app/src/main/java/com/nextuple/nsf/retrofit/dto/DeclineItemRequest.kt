package com.nextuple.nsf.retrofit.dto

data class DeclineItemRequest(
	val taskId: Long,
	val sku: String,
	val declinedQty: Int,
	val declineReason: String,
	val declineReasonText: String
)
