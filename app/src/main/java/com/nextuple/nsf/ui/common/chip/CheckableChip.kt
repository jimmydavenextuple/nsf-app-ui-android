package com.nextuple.nsf.ui.common.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R.drawable
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.conditional

@Composable
fun CheckableChip(
	modifier: Modifier = Modifier,
	text: String,
	isChecked: Boolean
) {
	Box(
		modifier = modifier
			.height(32.dp)
			.width(IntrinsicSize.Max)
			.conditional(
				condition = isChecked,
				onTrue = {
					background(color = BrandColor.BLUE_250_NT, shape = RoundedCornerShape(8.dp))
				},
				onFalse = {
					background(BrandColor.WHITE, shape = RoundedCornerShape(8.dp))
						.border(
							width = 1.dp,
							color = BrandColor.GRAY_900,
							shape = RoundedCornerShape(8.dp)
						)
				}
			),
		contentAlignment = Alignment.Center
	) {
		Row(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp, vertical = 4.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			if (isChecked) {
				Icon(
					modifier = Modifier.size(width = 12.dp, height = 9.dp),
					imageVector = ImageVector.vectorResource(drawable.ic_check_white),
					tint = BrandColor.WHITE,
					contentDescription = null
				)
			}
			Text(
				text = text.uppercase(),
				color = if (isChecked) BrandColor.WHITE else BrandColor.GRAY_900,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight(700),
				fontSize = 10.sp
			)
		}
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewCheckableChip() {
	CheckableChip(text = "All", isChecked = false)
}

@Composable
@Preview(showBackground = true)
fun PreviewCheckableChipChecked() {
	CheckableChip(text = "SDD", isChecked = true)
}
