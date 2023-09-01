package com.nextuple.nsf.retrofit.dto

data class PickItemRequest(
	val taskId: Long,
	val sku: String,
	val scannedUpc: String,
	val pickedQty: Int,
	val pickedLocation: String? = null
)
