package com.nextuple.nsf.ui.common.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun OverflowChip(
	modifier: Modifier = Modifier,
	overflowCount: Int,
	overflowPrefix: String = "+",
	overflowUnitText: String = "More"
) {
	Box(
		modifier = modifier
			.background(color = BrandColor.BLUE_800_NT, shape = RoundedCornerShape(4.dp))
			.padding(8.dp),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = "$overflowPrefix$overflowCount $overflowUnitText".uppercase(),
			color = BrandColor.GRAY_50,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight(700),
			fontSize = 10.sp,
			letterSpacing = 1.sp
		)
	}
}

@Composable
@Preview
fun PreviewOverflowChip() {
	OverflowChip(overflowCount = 2)
}

@Composable
@Preview
fun PreviewOverflowChipLots() {
	OverflowChip(overflowCount = 99)
}
