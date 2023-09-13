package com.nextuple.nsf.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

/**
 * TODO: Figure out asserting colors & icons and/or add snapshot testing.
 */
@Composable
fun CallToAction(
	modifier: Modifier = Modifier,
	iconTint: Color = Color.White,
	callToActionMode: CallToActionMode,
	onClick: () -> Unit = {}
) {
	FloatingActionButton(
		modifier = modifier.testTag("callToAction"),
		shape = CircleShape,
		containerColor = BrandColor.PINK_NT,
		onClick = onClick
	) {
		if (callToActionMode is CallToActionMode.Loading) {
			CircularProgressIndicator(
				modifier = Modifier
					.wrapContentSize()
					.padding(16.dp),
				color = BrandColor.PINK_100_NT,
				strokeWidth = 6.dp
			)
		} else {
			Icon(
				modifier = Modifier
					.fillMaxSize()
					.padding(16.dp),
				imageVector = ImageVector.vectorResource(callToActionMode.imageResource()),
				tint = iconTint,
				contentDescription = null // TODO: Provide
			)
		}
	}
}

@Composable
@Preview
fun PreviewCallToAction() {
	CallToAction(
		modifier = Modifier.size(76.dp),
		callToActionMode = CallToActionMode.Loading()
	)
}

sealed class CallToActionMode(val color: Color) {
	/**
	 * Initial state and Displays scan image
	 */
	class Scan(
		color: Color = BrandColor.PINK_NT
	) : CallToActionMode(color)

	/**
	 * Once clicking on scan option changes to Loading state
	 */
	class Loading(color: Color = BrandColor.PINK_NT) : CallToActionMode(color)

	/**
	 * Once scanning is completed changes from Loading -> Done
	 */
	class Done(
		color: Color = BrandColor.BLUE_300_NT
	) : CallToActionMode(color)
}

fun CallToActionMode.imageResource(): Int {
	return when (this) {
		is CallToActionMode.Done -> R.drawable.ic_check
		is CallToActionMode.Scan -> R.drawable.ic_scan
		is CallToActionMode.Loading -> -1
	}
}
