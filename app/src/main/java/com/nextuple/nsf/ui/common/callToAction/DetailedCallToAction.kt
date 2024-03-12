package com.nextuple.nsf.ui.common.callToAction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun DetailedCallToAction(
    modifier: Modifier = Modifier,
    detailedCallToActionMode: DetailedCallToActionMode,
    onClick: () -> Unit = {}
) {
	Box(
		modifier = modifier
			.height(64.dp)
			.background(
				color = detailedCallToActionMode.backgroundColor,
				shape = RoundedCornerShape(48.dp)
			)
			.padding(start = 20.dp, end = 20.dp)
			.clickable { onClick() }
			.testTag("detailedCallToAction")
	) {
		when (detailedCallToActionMode) {
			is DetailedCallToActionMode.Scan -> {
				Info(
					detailedCallToActionMode.text,
					detailedCallToActionMode
				)
			}

			is DetailedCallToActionMode.Loading -> {
				LoadingSpinner(detailedCallToActionMode)
			}

			is DetailedCallToActionMode.Done -> {
				Done(detailedCallToActionMode)
			}

			is DetailedCallToActionMode.Decline -> {
				Info(
					detailedCallToActionMode.text,
					detailedCallToActionMode
				)
			}
		}
	}
}

@Composable
private fun Info(
	text: String,
	detailedCallToActionMode: DetailedCallToActionMode
) {
	Row(
		modifier = Modifier
			.wrapContentSize()
			.padding(vertical = 16.dp)
			.testTag("detailedCallToAction_Scan"),
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically
	) {
		CallToActionIcon(detailedCallToActionMode = detailedCallToActionMode)

		Text(
			modifier = Modifier.padding(start = 4.dp),
			text = text.uppercase(),
			fontSize = 16.sp,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight(700),
			color = detailedCallToActionMode.contentColor,
			letterSpacing = 1.5.sp
		)
	}
}

@Composable
private fun LoadingSpinner(detailedCallToActionMode: DetailedCallToActionMode) {
	CircularProgressIndicator(
		modifier = Modifier
			.wrapContentSize()
			.padding(vertical = 16.dp, horizontal = 28.dp)
			.testTag("detailedCallToAction_LoadingSpinner"),
		color = detailedCallToActionMode.contentColor,
		strokeWidth = 6.dp
	)
}

@Composable
private fun Done(detailedCallToActionMode: DetailedCallToActionMode) {
	Box(
		modifier = Modifier
			.padding(vertical = 16.dp, horizontal = 28.dp)
			.testTag("detailedCallToAction_Done")
	) {
		CallToActionIcon(detailedCallToActionMode = detailedCallToActionMode)
	}
}

@Composable
private fun CallToActionIcon(detailedCallToActionMode: DetailedCallToActionMode) {
	Icon(
		modifier = Modifier.size(40.dp),
		imageVector = ImageVector.vectorResource(detailedCallToActionMode.imageResource()),
		tint = detailedCallToActionMode.contentColor,
		contentDescription = null
	)
}

@Composable
@Preview
private fun PreviewCallToActionLoading() {
	DetailedCallToAction(
		detailedCallToActionMode = DetailedCallToActionMode.Loading()
	)
}

@Composable
@Preview
private fun PreviewCallToActionScan() {
	DetailedCallToAction(
		detailedCallToActionMode = DetailedCallToActionMode.Scan("Pick 1")
	)
}

@Composable
@Preview
private fun PreviewCallToActionDone() {
	DetailedCallToAction(
		detailedCallToActionMode = DetailedCallToActionMode.Done()
	)
}

@Composable
@Preview
private fun PreviewCallToActionDecline() {
	DetailedCallToAction(
		detailedCallToActionMode = DetailedCallToActionMode.Decline()
	)
}
