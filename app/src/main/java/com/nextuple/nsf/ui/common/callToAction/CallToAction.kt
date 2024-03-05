package com.nextuple.nsf.ui.common.callToAction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
		containerColor = callToActionMode.color,
		elevation = FloatingActionButtonDefaults.elevation(
			defaultElevation = 0.dp,
			pressedElevation = 0.dp,
			focusedElevation = 0.dp,
			hoveredElevation = 0.dp
		),
		onClick = onClick
	) {
		if (callToActionMode is CallToActionMode.Loading) {
			CircularProgressIndicator(
				modifier = Modifier
					.wrapContentSize()
					.padding(16.dp),
				color = BrandColor.GRAY_50,
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
private fun PreviewCallToActionLoading() {
	CallToAction(
		modifier = Modifier.size(76.dp),
		callToActionMode = CallToActionMode.Scan()
	)
}

@Composable
@Preview
private fun PreviewCallToActionScan() {
	CallToAction(
		modifier = Modifier.size(76.dp),
		callToActionMode = CallToActionMode.Loading()
	)
}

@Composable
@Preview
private fun PreviewCallToActionDone() {
	CallToAction(
		modifier = Modifier.size(76.dp),
		callToActionMode = CallToActionMode.Done()
	)
}
