package com.nextuple.nsf.retrofit.dto.response

data class DemoCreateFRResponse(
    val fulfillmentRequestNumber: String,
    val customerOrderNo: String,
    val fulfillmentType: String,
    val subFulfillmentType: String,
)
