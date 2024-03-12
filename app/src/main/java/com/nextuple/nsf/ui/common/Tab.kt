package com.nextuple.nsf.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun Tab(
	modifier: Modifier = Modifier,
	title: String,
	count: Int? = null,
	isSelected: Boolean = false,
	onSelect: () -> Unit = {}
) {
	Box(
		modifier = modifier
			.width(IntrinsicSize.Max)
			.clickable(onClick = onSelect)
	) {
		Row(
			modifier = Modifier.padding(12.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			if (count != null) {
				Text(
					text = count.toString(),
					fontSize = 12.sp,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight(700),
					color = BrandColor.GRAY_650,
					textAlign = TextAlign.Center,
					letterSpacing = 1.5.sp
				)
			}
			Text(
				text = title.uppercase(),
				fontSize = 12.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight(700),
				color = BrandColor.GRAY_900,
				textAlign = TextAlign.Center,
				letterSpacing = 1.5.sp
			)
		}
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(4.dp)
				.background(if (isSelected) BrandColor.BLUE_300_NT else BrandColor.GRAY_500)
				.align(Alignment.BottomStart)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewTab() {
	Tab(title = "In Progress")
}

@Preview(showBackground = true)
@Composable
fun PreviewTabSelected() {
	Tab(title = "In Progress", isSelected = true)
}

@Preview(showBackground = true)
@Composable
fun PreviewTabWithCount() {
	Tab(title = "In Progress", count = 6, isSelected = true)
}
