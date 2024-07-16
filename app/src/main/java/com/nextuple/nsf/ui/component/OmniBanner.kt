package com.nextuple.nsf.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.AppMessage

@Composable
fun OmniBanner(
	modifier: Modifier = Modifier,
	appMessage: AppMessage,
	onDismiss: () -> Unit = {}
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.height(IntrinsicSize.Min)
			.background(
				color = appMessage.messageType.backgroundColor
			),
		horizontalArrangement = Arrangement.Start,
		verticalAlignment = Alignment.Top
	) {
		Box(
			modifier = Modifier
				.width(8.dp)
				.fillMaxHeight()
				.background(
					color = appMessage.messageType.statusColor
				)
		)
		Icon(
			modifier = Modifier.padding(8.dp),
			imageVector = ImageVector.vectorResource(appMessage.messageType.icon),
			tint = appMessage.messageType.statusColor,
			contentDescription = "Error Icon"
		)
		Column(
			modifier = Modifier
				.padding(vertical = 8.dp)
				.weight(1f),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			val textStyle = TextStyle(
				lineHeight = 15.sp,
				fontSize = 14.sp,
				letterSpacing = 0.5.sp
			)
			Text(
				text = appMessage.headerText,
				style = textStyle.copy(fontWeight = FontWeight(700))
			)
			Text(
				text = appMessage.detailText,
				style = textStyle.copy(fontWeight = FontWeight(400))
			)
		}
		Icon(
			modifier = Modifier
				.padding(8.dp)
				.size(20.dp)
				.clickable { onDismiss() },
			imageVector = ImageVector.vectorResource(R.drawable.ic_close),
			tint = BrandColor.GRAY_900,
			contentDescription = "close"
		)
	}
}

@Preview
@Composable
fun OmniBannerPreviewError() {
	OmniBanner(
		appMessage = AppMessage.printHoldSlipError
	)
}

// Placeholders for the previews for the other message types
/*
@Preview
@Composable
fun OmniBannerPreviewSuccess() {
	OmniBanner(
		omniMessage = OmniMessage(
			headerText = "Couldn’t print hold slip",
			detailText = "Try again later, or if issues persist, contact the service desk.",
			messageType = OmniMessageType.SUCCESS
		)
	)
}

@Preview
@Composable
fun OmniBannerPreviewInfo() {
	OmniBanner(
		omniMessage = OmniMessage(
			headerText = "Couldn’t print hold slip",
			detailText = "Try again later, or if issues persist, contact the service desk.",
			messageType = OmniMessageType.INFO
		)
	)
}*/
