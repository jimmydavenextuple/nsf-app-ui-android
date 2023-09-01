package com.nextuple.nsf.retrofit.dto

data class StageTask(
	val id: Long,
	val status: Status? = null,
	val assignedUserId: String? = null, // INFO: Future-proofing

	// INFO: UI needs this to Reprint Hold Slip
	val fulfillmentRequestNumber: String,
	val brand: String? = null,
	val store: String? = null,
	val fulfillmentType: String? = null,
	val subFulfillmentType: String? = null,

	val orderNumber: String? = null,
	val orderDate: String? = null,
	val orderInputSource: String? = null,
	val athleteFirstName: String? = null,
	val athleteLastName: String? = null,

	val holdSlipZPL: MutableList<String> = mutableListOf(),

	var totalContainerCount: Int? = null,
	val containers: List<StageTaskContainer>
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
	val scannedUpc: String? = null
)

data class Status(
	val code: String? = null,
	val name: String? = null
)
