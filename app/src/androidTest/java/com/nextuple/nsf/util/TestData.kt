package com.nextuple.nsf.util

import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse

object TestData {

	val PACK_TASK_ITEM = PackTaskItem(
		id = 1,
		packedQty = 2,
		sku = "sku",
		qty = 1,
		declinedQty = 2,
		productName = "name",
		productImageUrls = emptyList()
	)

	val SAMPLE_ORDERS_RESPONSE = listOf(
		OrderDetailsResponse(
			fulfillmentRequestDetail = FulfillmentRequestDetail(
				fulfillmentRequestNumber = "123"
			)
		),
		OrderDetailsResponse(
			fulfillmentRequestDetail = FulfillmentRequestDetail(
				fulfillmentRequestNumber = "123"
			)
		)
	)
}
