package com.nextuple.nsf.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun TertiaryButton(
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	text: String,
	textColor: Color = BrandColor.DARK_BLUE,
	tag: String = "TertiaryButton",
	onButtonClick: () -> Unit
) {
	Text(
		modifier = modifier
			.clickable { if (enabled) onButtonClick() }
			.testTag(tag),
		text = text,
		color = if (enabled) textColor else BrandColor.GRAY_600,
		textDecoration = TextDecoration.Underline,
		fontSize = 12.sp,
		letterSpacing = 1.5.sp,
		fontWeight = FontWeight.Bold
	)
}

@Preview(showBackground = true)
@Composable
fun TertiaryButtonPreview() {
	TertiaryButton(text = "TERTIARY") { }
}
