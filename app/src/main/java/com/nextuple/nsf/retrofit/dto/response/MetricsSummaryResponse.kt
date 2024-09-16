package com.nextuple.nsf.retrofit.dto.response

data class MetricsSummaryResponse(
	val bopis: OrderMetrics? = null,
	val bopl: OrderMetrics? = null
) {
	data class OrderMetrics(
		val fillRatePercentage: Double? = null,
		val productivityPercentage: Double? = null,
		val unitsWorkedInTime: Int? = null,
		val totalUnitsWorked: Int? = null,
		val pickUph: Double? = null,
		val curbsideDeliverySpeed: String? = null,
		val inStoreDeliverySpeed: String? = null
	)
}
