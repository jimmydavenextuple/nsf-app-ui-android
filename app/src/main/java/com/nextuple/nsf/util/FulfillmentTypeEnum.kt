package com.nextuple.nsf.util

enum class FulfillmentType {
	BOPIS,
	SFS
}

enum class SubFulfillmentType(
	val defaultLocation: String = "",
	val subFulfillmentTypeName: String = ""
) {
	BOPIS(defaultLocation = "Sales Floor", subFulfillmentTypeName = "BOPIS"),
	BOPL(defaultLocation = "BOPL Area", subFulfillmentTypeName = "BOPL"),
	SFS(subFulfillmentTypeName = "SFS"),
	LTL(subFulfillmentTypeName = "LTL"),
	SAME_DAY(defaultLocation = "Sales Floor", subFulfillmentTypeName = "SDD");

	override fun toString(): String {
		return subFulfillmentTypeName
	}
}
