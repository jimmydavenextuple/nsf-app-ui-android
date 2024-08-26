package com.nextuple.nsf.util

enum class FulfillmentType {
	BOPIS,
	SAME_DAY,
	SFS
}

enum class SubFulfillmentType(
	val defaultLocation: String = "",
	val subFulfillmentTypeName: String = ""
) {
	BOPIS(defaultLocation = "Sales Floor", subFulfillmentTypeName = "BOPIS"),
	BOPL(defaultLocation = "BOPL Area", subFulfillmentTypeName = "BOPL"),
	LTL(subFulfillmentTypeName = "LTL"),
	SAME_DAY(defaultLocation = "Sales Floor", subFulfillmentTypeName = "SAME_DAY"),
	SFS(subFulfillmentTypeName = "SFS");

	override fun toString(): String {
		return subFulfillmentTypeName
	}
}
