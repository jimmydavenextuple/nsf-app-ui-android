package com.nextuple.nsf.retrofit.dto.response

data class GetDeclineCodesResponse(
    val pickDeclineCodes: List<DeclineCode>,
    val pickupDeclineCodes: List<DeclineCode>
)

data class DeclineCode(
	val id: String,
	val displayName: String
)
