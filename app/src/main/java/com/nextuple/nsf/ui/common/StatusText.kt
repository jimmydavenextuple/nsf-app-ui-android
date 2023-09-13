package com.nextuple.nsf.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun StatusText(
	modifier: Modifier = Modifier,
	status: String,
	backgroundColor: Color,
	shape: Shape = RoundedCornerShape(32.dp)
) {
	Box(
		modifier = modifier.background(color = backgroundColor, shape = shape)
	) {
		Text(
			modifier = Modifier.padding(12.dp, 2.dp).testTag("StatusText"),
			text = status,
			color = BrandColor.GRAY_50,
			fontSize = 16.sp,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Bold,
			letterSpacing = 0.5.sp
		)
	}
}

@Composable
@Preview
fun PreviewOrderStatusText() {
	StatusText(status = "Order Ready", backgroundColor = BrandColor.BLUE_300_NT)
}
