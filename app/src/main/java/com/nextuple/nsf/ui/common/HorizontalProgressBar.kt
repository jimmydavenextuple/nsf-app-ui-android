package com.nextuple.nsf.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun HorizontalProgressBar(
	modifier: Modifier = Modifier,
	title: String,
	workedCount: Int?,
	totalCount: Int?,
	percent: Double? = null,
	completedColor: Color = BrandColor.BLUE_300_NT
) {
	var progress by remember { mutableStateOf(0f) }

	progress =
		percent?.toFloat() ?: ((workedCount?.toFloat() ?: 1f) / (totalCount?.toFloat() ?: 1f))

	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Text(
			text = title.uppercase(),
			color = BrandColor.BLACK,
			fontSize = 12.sp,
			fontWeight = FontWeight(700),
			fontFamily = FontFamily.ARCHIVO,
			letterSpacing = 1.5.sp
		)

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight(),
			contentAlignment = Alignment.CenterStart
		) {
			// Background of the bar
			Box(
				modifier = Modifier
					.fillMaxSize()
					.clip(RoundedCornerShape(8.dp))
					.background(BrandColor.GRAY_SPECIAL_SUBTLE)
			) {
				if (workedCount == 0) {
					Text(
						modifier = Modifier
							.testTag("HorizontalProgressBar_Progress_Empty")
							.fillMaxHeight()
							.padding(top = 2.dp, start = 8.dp),
						text = "0/$totalCount",
						textAlign = TextAlign.Center,
						color = BrandColor.BLACK,
						fontSize = 12.sp,
						fontWeight = FontWeight(700),
						fontFamily = FontFamily.ARCHIVO,
						letterSpacing = 1.5.sp
					)
				}
			}
			// Current progress
			Box(
				modifier = Modifier
					.fillMaxWidth(progress)
					.fillMaxHeight()
					.clip(RoundedCornerShape(8.dp))
					.background(completedColor)
					.animateContentSize()
			) {
				if ((workedCount ?: 0) > 0) {
					Text(
						modifier = Modifier
							.testTag("HorizontalProgressBar_Progress")
							.fillMaxHeight()
							.padding(top = 2.dp, end = 8.dp)
							.align(Alignment.CenterEnd),
						text = "$workedCount/$totalCount",
						textAlign = TextAlign.Center,
						color = BrandColor.WHITE,
						fontSize = 12.sp,
						fontWeight = FontWeight(700),
						fontFamily = FontFamily.ARCHIVO,
						letterSpacing = 1.5.sp
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun HorizontalProgressBarEmptyPreview() {
	HorizontalProgressBar(
		modifier = Modifier
			.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
			.height(17.dp),
		title = "Units",
		workedCount = 0,
		totalCount = 5
	)
}

@Preview(showBackground = true)
@Composable
fun HorizontalProgressBarPartialPreview() {
	HorizontalProgressBar(
		modifier = Modifier
			.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
			.height(17.dp),
		title = "Units",
		workedCount = 3,
		totalCount = 5
	)
}

@Preview(showBackground = true)
@Composable
fun HorizontalProgressBarCompletePreview() {
	HorizontalProgressBar(
		modifier = Modifier
			.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
			.height(17.dp),
		title = "Units",
		workedCount = 5,
		totalCount = 5
	)
}
