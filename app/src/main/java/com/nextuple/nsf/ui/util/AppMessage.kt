package com.nextuple.nsf.ui.util

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

data class AppMessage(
	val headerText: String,
	val detailText: String,
	val messageType: AppMessageType
) {
	// Determine if this is the best place to maintain these
	// Error Constant file?
	companion object {
		val getHoldSlipError = AppMessage(
			messageType = AppMessageType.ERROR,
			headerText = "Couldn’t retrieve hold slip data",
			detailText = "Try again later, or if issues persist, contact the service desk."
		)

		val printHoldSlipError = AppMessage(
			messageType = AppMessageType.ERROR,
			headerText = "Couldn't Print Hold Slip",
			detailText = "Check your network connection or your printer settings."
		)
	}
}

enum class AppMessageType(
	@DrawableRes val icon: Int,
	val backgroundColor: Color,
	val statusColor: Color
) {
	// TODO: Add these when designs are created
	//    INFO(),
	//    SUCCESS(),
	ERROR(
		icon = R.drawable.ic_close_red,
		statusColor = BrandColor.RED_600,
		backgroundColor = BrandColor.RED_100
	)
}
