package com.nextuple.nsf.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun TextInfo(
	modifier: Modifier = Modifier,
	label: String,
	value: String?,
	valueMaxLines: Int = Int.MAX_VALUE
) {
	Column(modifier = modifier) {
		Text(
			text = label.uppercase(),
			fontFamily = FontFamily.ARCHIVO,
			fontSize = 12.sp,
			fontWeight = FontWeight.Bold,
			letterSpacing = 1.5.sp
		)
		Text(
			text = value.orEmpty(),
			fontFamily = FontFamily.ARCHIVO,
			fontSize = 12.sp,
			fontWeight = FontWeight.Normal,
			letterSpacing = 0.5.sp,
			maxLines = valueMaxLines
		)
	}
}

@Preview
@Composable
fun PreviewTextInfo() {
	TextInfo(label = "label", value = "value value value")
}
