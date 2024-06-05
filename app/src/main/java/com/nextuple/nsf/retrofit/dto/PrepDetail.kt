package com.nextuple.nsf.retrofit.dto

data class PrepDetail(
	val fulfillmentType: String,
	val subFulfillmentType: String,
	val fulfillmentRequestNumber: String,
	val orderNumber: String? = null,
	val athleteFirstName: String? = null,
	val athleteLastName: String? = null,

	var pickedBy: String? = null,
	val items: List<PackTaskItem>,

	val assembleTask: FITTask? = null,
	val packTask: FITTask? = null,
	val stageTask: FITTask? = null,
	val holdSlipZPL: MutableList<String> = mutableListOf()
)

data class PackTaskItem(
	val sku: String,
	val primaryAttr: ProductAttribute? = null,
	val secondaryAttr: ProductAttribute? = null,
	val tertiaryAttr: ProductAttribute? = null,
	val qty: Int,
	val declinedQty: Int = 0,
	val productName: String,
	val productImageUrls: List<String>,
	val scannedBarcode: String? = null,
	@Transient
	var isScanned: Boolean = false,
	@Transient
	var isDeclined: Boolean = false
)

data class FITTask(
	val taskId: Long? = null,
	val status: Status
) {
	companion object {
		fun isInProgress(code: String): Boolean = code in setOf("1005", "1010")
	}
}
