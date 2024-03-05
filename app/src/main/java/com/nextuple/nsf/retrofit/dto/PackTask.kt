package com.nextuple.nsf.retrofit.dto

data class PackTask(
	val id: Long,
	val assignedUserId: String,
	val fulfillmentRequestNumber: String,
	val orderNumber: String? = null,
	val athleteFirstName: String? = null,
	val athleteLastName: String? = null,
	var totalQty: Int,
	var packedQty: Int? = null,
	var declinedQty: Int? = null,
	var totalWorkedQty: Int? = null,
	var totalRemainingQty: Int? = null,
	val items: List<PackTaskItem> = emptyList()
)

data class PackTaskItem(
    val id: Long,
    val sku: String,
    val primaryAttr: ProductAttribute? = null,
    val secondaryAttr: ProductAttribute? = null,
    val tertiaryAttr: ProductAttribute? = null,
    val qty: Int,
    val packedQty: Int,
    val declinedQty: Int,
    val productName: String,
    val productImageUrls: List<String>,
    val scannedBarcode: String? = null,

    @Transient
	var isScanned: Boolean = false
)
