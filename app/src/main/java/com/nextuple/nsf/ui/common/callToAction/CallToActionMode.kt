package com.nextuple.nsf.ui.common.callToAction

import androidx.compose.ui.graphics.Color
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

sealed class CallToActionMode(val color: Color) {
	/**
	 * Initial state and Displays scan image
	 */
	class Scan(
		color: Color = BrandColor.BLUE_300_NT
	) : CallToActionMode(color)

	/**
	 * Once clicking on scan option changes to Loading state
	 */
	class Loading(color: Color = BrandColor.BLUE_300_NT) : CallToActionMode(color)

	/**
	 * Once scanning is completed changes from Loading -> Done
	 */
	class Done(
		color: Color = BrandColor.DARK_BLUE
	) : CallToActionMode(color)
}

fun CallToActionMode.imageResource(): Int {
	return when (this) {
		is CallToActionMode.Done -> R.drawable.ic_check
		is CallToActionMode.Scan -> R.drawable.ic_scan
		is CallToActionMode.Loading -> -1
	}
}
