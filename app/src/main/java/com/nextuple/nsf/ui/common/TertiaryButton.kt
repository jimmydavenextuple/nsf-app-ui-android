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
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun TertiaryButton(
	modifier: Modifier = Modifier,
	text: String,
	textColor: Color = BrandColor.GRAY_900,
	tag: String = "TertiaryButton",
	onButtonClick: () -> Unit
) {
	Text(
		modifier = modifier.clickable { onButtonClick() }.testTag(tag),
		text = text,
		color = textColor,
		textDecoration = TextDecoration.Underline,
		fontFamily = FontFamily.ARCHIVO,
		letterSpacing = 1.5.sp,
		fontWeight = FontWeight.Bold
	)
}

@Preview(showBackground = true)
@Composable
fun TertiaryButtonPreview() {
	TertiaryButton(text = "TERTIARY") { }
}
