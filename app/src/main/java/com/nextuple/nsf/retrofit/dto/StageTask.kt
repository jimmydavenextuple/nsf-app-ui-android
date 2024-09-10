package com.nextuple.nsf.retrofit.dto

/**
 * TODO: Refactor away the need for this class.
 */
data class StageTask(
	val id: Long,
	val status: Status? = null,

	val fulfillmentRequestNumber: String,

	val holdSlipZPL: MutableList<String> = mutableListOf(),

	val containers: List<StageTaskContainer> = emptyList()
)

data class StageTaskContainer(
	val id: Long,
	val holdingLocation: String? = null,
	val packedItems: List<PackedItem> = emptyList()
)

data class PackedItem(
	val sku: String,
	val productName: String,
	val productImageUrls: List<String>,
	val primaryAttr: ProductAttribute? = null,
	val secondaryAttr: ProductAttribute? = null,
	val tertiaryAttr: ProductAttribute? = null,
	val qty: Int,
	val scannedUpc: String? = null,
	var originalItem: PackTaskItem? = null,
)

data class Status(
	val code: String? = null,
	val name: String? = null
)
