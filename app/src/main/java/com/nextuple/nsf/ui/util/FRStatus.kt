package com.nextuple.nsf.ui.util

/**
 * Enum containing the status that comes from the backend.
 * Currently one for one with enum in api with the same name
 */
enum class FRStatus(
	val displayName: String,
	val code: String
) {
	PICKUP_READY("Ready", "1075"),
	PICKUP_EXTENDED("Extended", "1075"),
	PICKUP_AGED("Aged", "1075"),
	ATHLETE_CHECKED_IN("", "1080"),
	PICKUP_STARTED("In Progress", "1090"),
	PICKUP_COMPLETED("Completed", "1095")
}
