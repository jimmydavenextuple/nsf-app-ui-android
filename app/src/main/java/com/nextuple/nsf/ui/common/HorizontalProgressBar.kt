package com.nextuple.nsf.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun HorizontalProgressBar(
	modifier: Modifier = Modifier,
	unitsCompleted: Int,
	totalUnits: Int,
	title: String,
	textColor: Color = BrandColor.BLACK,
	fontSize: TextUnit = 11.sp,
	isCustomTitle: Boolean = false
) {
	var progress by remember { mutableStateOf(0f) }

	var progressTitle by remember {
		mutableStateOf("")
	}

	progress = unitsCompleted.toFloat() / totalUnits.toFloat()
	progressTitle = if (isCustomTitle) title else "$unitsCompleted/$totalUnits $title"

	Column(
		modifier = modifier
	) {
		// text above the progressBar
		Text(
			text = progressTitle,
			color = textColor,
			modifier = Modifier.padding(bottom = 2.dp).testTag("ProgressBarTitle"),
			fontSize = fontSize,
			fontWeight = FontWeight.Bold,
			fontFamily = FontFamily.ARCHIVO,
			letterSpacing = 1.5.sp
		)

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(6.dp),
			contentAlignment = Alignment.CenterStart
		) {
			// background of progress bar
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(4.dp)
					.clip(RoundedCornerShape(9.dp))
					.background(BrandColor.GRAY_200)
					.padding(8.dp)

			)
			// progress
			Box(
				modifier = Modifier
					.fillMaxWidth(progress)
					.fillMaxHeight()
					.clip(RoundedCornerShape(9.dp))
					.background(BrandColor.BLUE_250_NT)
					.animateContentSize()
					.alpha(0.2f)
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun HorizontalProgressBarPreview() {
	HorizontalProgressBar(
		modifier = Modifier
			.fillMaxWidth()
			.height(IntrinsicSize.Min)
			.padding(vertical = 8.dp, horizontal = 30.dp),
		unitsCompleted = 1,
		totalUnits = 3,
		title = "UNITS WORKED"
	)
}
