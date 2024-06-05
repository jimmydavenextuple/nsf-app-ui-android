package com.nextuple.nsf.retrofit.api

import com.nextuple.nsf.retrofit.dto.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ConfigApi {
	@GET("v1/config/{store}")
	suspend fun getStoreConfig(
		@Header("userId") userId: String,
		@Path("store") store: String
	): ApiResponse<StoreConfig>
}

data class StoreConfig(
	val holdingLocations: List<String> = emptyList(),
	val pickSymbologyPrefixes: List<String> = emptyList()
) {
	companion object {
		val DEFAULT_HOLDING_LOCATIONS = listOf(
			"Main Holding Area",
			"Front Counter",
			"Back Room",
			"Alternate - 1",
			"Alternate - 2"
		)
		val PICK_ITEM_SYMBOLOGY_PREFIXES = setOf("upc", "ean", "code39", "code128")
	}
}
