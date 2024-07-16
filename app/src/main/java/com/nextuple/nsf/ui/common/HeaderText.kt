package com.nextuple.nsf.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun HeaderText(modifier: Modifier = Modifier, title: String, value: String) {
	Column(modifier = modifier) {
		Text(
			text = title,
			fontWeight = FontWeight.Bold,
			fontSize = 14.sp,
			letterSpacing = 1.5.sp
		)
		Text(
			text = value,
			fontWeight = FontWeight.Normal,
			fontSize = 14.sp,
			letterSpacing = 0.5.sp,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun HeaderTextPreview() {
	HeaderText(title = "Customer", value = "Customer Name")
}
