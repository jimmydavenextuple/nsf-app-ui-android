package com.nextuple.nsf.util

enum class FulfillmentType {
	BOPIS,
	SFS
}

enum class SubFulfillmentType(
	val defaultLocation: String = ""
) {
	BOPIS(defaultLocation = "Sales Floor"),
	BOPL(defaultLocation = "BOPL Area"),
	SFS,
	LTL,
	SAME_DAY
}
