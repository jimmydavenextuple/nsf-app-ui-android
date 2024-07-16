package com.nextuple.nsf.ui.common.callToAction

import androidx.compose.ui.graphics.Color
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

sealed class DetailedCallToActionMode(
	val backgroundColor: Color,
	val contentColor: Color
) {
	/**
	 * Initial state and Displays scan image
	 */
	class Scan(
		val text: String,
		backgroundColor: Color = BrandColor.DARK_BLUE,
		contentColor: Color = BrandColor.TRANSPARENT
	) : DetailedCallToActionMode(backgroundColor, contentColor)

	/**
	 * Once clicking on scan option changes to Loading state
	 */
	class Loading(
		backgroundColor: Color = BrandColor.TRANSPARENT,
		contentColor: Color = BrandColor.DARK_BLUE
	) : DetailedCallToActionMode(backgroundColor, contentColor)

	/**
	 * Once scanning is completed changes from Loading -> Done
	 */
	class Done(
		backgroundColor: Color = BrandColor.DARK_BLUE,
		contentColor: Color = BrandColor.TRANSPARENT
	) : DetailedCallToActionMode(backgroundColor, contentColor)

	/**
	 * Once item is declined
	 */
	class Decline(
		val text: String = "Declined",
		backgroundColor: Color = BrandColor.GRAY_800,
		contentColor: Color = BrandColor.TRANSPARENT
	) : DetailedCallToActionMode(backgroundColor, contentColor)
}

fun DetailedCallToActionMode.imageResource(): Int {
	return when (this) {
		is DetailedCallToActionMode.Done -> R.drawable.ic_check
		is DetailedCallToActionMode.Scan -> R.drawable.ic_pick_qr_code_scanner
		is DetailedCallToActionMode.Decline -> R.drawable.ic_close
		is DetailedCallToActionMode.Loading -> -1
	}
}
