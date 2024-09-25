package com.nextuple.nsf.retrofit.dto

import com.nextuple.nsf.util.FulfillmentType
import com.nextuple.nsf.util.SubFulfillmentType
import java.time.Instant

data class PickTask(
	val id: Long,
	val fulfillmentType: FulfillmentType,
	val subFulfillmentType: SubFulfillmentType,
	var items: List<PickTaskItem>,
	val pickedQty: Int? = null,
	val declinedQty: Int? = null,
	val totalWorkedQty: Int,
	val totalQty: Int,
	val totalRemainingQty: Int
)

data class PickTaskItem(
	val sku: String,
	val upcs: List<String>,
	val style: String?,
	val additionalAttributes: Map<String, String>? = emptyMap(),
	val locations: List<String> = emptyList(),
	val onHandQty: Int?,
	val qty: Int,
	val pickedQty: Int,
	val declinedQty: Int,
	val productName: String,
	val productBrand: String,
	val productImageUrls: List<String> = emptyList(),
	val productHighResImageUrls: List<String> = emptyList(),
	val clearanceColorCode: String? = null,
	val clearanceColorDesc: String? = null,
	val clearanceColorRgb: String? = null,
	val lastReturn: Instant? = null,
	val lastReceived: Instant? = null,
	val substitutedSku: String? = null,
	var substitutionAllowed: Boolean = false,
	var substitutions: List<PickTaskItem>? = null,
	var originalItem: PickTaskItem? = null,
	var originalItemDeclineReason: String? = null,
	var originalItemDeclineReasonText: String? = null
) {
	fun getRemainingPickQty() = qty - pickedQty - declinedQty
}

data class ProductAttribute(
	val name: String,
	val value: String
)
