package com.nextuple.nsf.retrofit.dto

data class PickTask(
	val id: Long,
	val items: List<PickTaskItem>,
	val pickedQty: Int? = null,
	val declinedQty: Int? = null,
	val totalWorkedQty: Int,
	val totalQty: Int,
	val totalRemainingQty: Int
)

data class PickTaskItem(
	val sku: String,
	val upcs: List<String>,
	val primaryAttr: ProductAttribute? = null,
	val secondaryAttr: ProductAttribute? = null,
	val tertiaryAttr: ProductAttribute? = null,
	val locations: List<String> = emptyList(),
	val onHandQty: Int?,
	val qty: Int,
	val pickedQty: Int,
	val declinedQty: Int,
	val productName: String,
	val productBrand: String,
	val productImageUrls: List<String> = emptyList()
)

data class ProductAttribute(
	val name: String,
	val value: String
)
