package com.nextuple.nsf.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun CircularProgressBar(
	modifier: Modifier = Modifier,
	completedUnits: Int,
	inProgressUnits: Int,
	totalUnits: Int,
	progressBarSize: Dp = 240.dp,
	indicatorThickness: Dp = 20.dp,
	animationDuration: Int = 1000,
	animationDelay: Int = 0,
	backgroundIndicatorColor: Color = BrandColor.GRAY_300,
	completedIndicatorColor: Color = BrandColor.GREEN_500,
	inProgressIndicatorColor: Color = BrandColor.YELLOW_400,
	showLegends: Boolean = false,
	backgroundLegendText: String = "Unworked",
	completedLegendText: String = "Progress",
	inProgressLegendText: String = "Being Worked",
	centerText: String? = null,
	showCenterImage: Boolean = true,
	roundBorder: Boolean = true,
	centerIconModifier: Modifier = Modifier.clickable { },
	centerProgressTextStyle: TextStyle = TextStyle(
		fontWeight = FontWeight.Bold,
		fontFamily = FontFamily.ARCHIVO,
		letterSpacing = 1.5.sp,
		fontSize = MaterialTheme.typography.displayLarge.fontSize
	),
	centerTextStyle: TextStyle = TextStyle(
		fontWeight = FontWeight.SemiBold,
		fontFamily = FontFamily.ARCHIVO,
		letterSpacing = 1.5.sp,
		fontSize = MaterialTheme.typography.bodySmall.fontSize
	),
	legendsTextStyle: TextStyle = TextStyle(
		fontFamily = FontFamily.ARCHIVO,
		fontWeight = FontWeight.Medium,
		textAlign = TextAlign.Center,
		fontSize = 20.sp
	)
) {
	var completedProgressRemember by remember {
		mutableStateOf(0f)
	}

	var inProgressRemember by remember {
		mutableStateOf(0f)
	}

	// Number Animation
	val completedAnimateNumer = animateFloatAsState(
		targetValue = completedProgressRemember,
		animationSpec = tween(
			durationMillis = animationDuration,
			delayMillis = animationDelay
		)
	)
	val inProgressAnimateNumber = animateFloatAsState(
		targetValue = inProgressRemember,
		animationSpec = tween(
			durationMillis = animationDuration,
			delayMillis = animationDelay
		)
	)
	SideEffect {
		completedProgressRemember = if (totalUnits == 0) 0f else completedUnits * 100f / totalUnits
		inProgressRemember = if (totalUnits == 0) 0f else (inProgressUnits + completedUnits) * 100f / totalUnits
	}
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier.size(size = progressBarSize).testTag("CircularProgressBar")
		) {
			Canvas(
				modifier = Modifier.size(size = progressBarSize)
			) {
				val canvasSize = size.minDimension

				val radius =
					canvasSize / 2 - maxOf(indicatorThickness, indicatorThickness).toPx() / 2

				// Background circle
				drawCircle(
					color = backgroundIndicatorColor,
					radius = radius,
					style = Stroke(width = indicatorThickness.toPx(), cap = StrokeCap.Round)
				)

				val completedUnitsSweepAngle = (completedAnimateNumer.value / 100) * 360
				val inProgressUnitsSweepAngle = (inProgressAnimateNumber.value / 100) * 360

				drawArc(
					color = inProgressIndicatorColor,
					startAngle = 270f,
					sweepAngle = inProgressUnitsSweepAngle,
					useCenter = false,
					topLeft = size.center - Offset(radius, radius),
					size = Size(radius * 2, radius * 2),
					style = Stroke(
						width = indicatorThickness.toPx(),
						cap = if (roundBorder) StrokeCap.Round else StrokeCap.Butt
					)
				)

				drawArc(
					color = completedIndicatorColor,
					startAngle = 270f,
					sweepAngle = completedUnitsSweepAngle,
					useCenter = false,
					topLeft = size.center - Offset(radius, radius),
					size = Size(radius * 2, radius * 2),
					style = Stroke(
						width = indicatorThickness.toPx(),
						cap = if (roundBorder) StrokeCap.Round else StrokeCap.Butt
					)
				)
			}

			// Display the text inside circle
			DisplayText(
				completedUnits = completedUnits,
				totalUnits = totalUnits,
				centerProgressTextStyle = centerProgressTextStyle,
				centerTextStyle = centerTextStyle,
				centerText = centerText,
				showCenterImage = showCenterImage,
				modifier = centerIconModifier

			)
		}
		if (showLegends) {
			Spacer(modifier = Modifier.height(24.dp))

			DisplayLegends(
				items = listOf(
					Legend(backgroundIndicatorColor, backgroundLegendText),
					Legend(completedIndicatorColor, completedLegendText),
					Legend(inProgressIndicatorColor, inProgressLegendText)
				),
				textStyle = legendsTextStyle
			)
		}
	}
}

@Composable
private fun DisplayText(
	completedUnits: Int,
	totalUnits: Int,
	centerProgressTextStyle: TextStyle,
	centerTextStyle: TextStyle,
	centerText: String?,
	showCenterImage: Boolean,
	modifier: Modifier
) {
	Column(
		modifier = Modifier.testTag("CircularProgressBarTextInside"),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// Text that shows the number inside the circle
		Text(
			text = "$completedUnits/$totalUnits",
			style = centerProgressTextStyle,
// 			fontFamily = FontFamily.SANS
			fontFamily = FontFamily.ARCHIVO
		)
		centerText?.let {
			Text(
				text = centerText,
				style = centerTextStyle,
				fontFamily = FontFamily.ARCHIVO,
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold,
				color = BrandColor.GRAY_600
			)
		}
		if (showCenterImage) {
			Spacer(modifier = Modifier.height(3.dp))
			DisplayImage(modifier = modifier)
		}
	}
}

@Composable
private fun DisplayImage(
	modifier: Modifier
) {
	Image(
		painter = painterResource(id = R.drawable.circular_progress_question_mark),
		contentDescription = null,
		modifier = modifier.testTag("CircularProgressBarCenterImage")
	)
}

@Composable
fun DisplayLegends(
	circleSize: Int = 24,
	space: Int = 8,
	items: List<Legend>,
	textStyle: TextStyle
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		items.forEach {
			Row(
				modifier = Modifier,
				horizontalArrangement = Arrangement.Start
			) {
				Box(
					modifier = Modifier
						.size(circleSize.dp)
						.clip(CircleShape)
						.background(color = it.color)
				)
				Spacer(modifier = Modifier.width(space.dp))
				Text(
					modifier = Modifier
						.padding(start = 5.dp)
						.wrapContentHeight()
						.align(Alignment.CenterVertically),
					text = it.text,
					style = textStyle
				)
			}
		}
	}
}

data class Legend(val color: Color, val text: String)

@Preview(showBackground = true)
@Composable
fun CircularProgressBarPreview() {
	CircularProgressBar(
		completedUnits = 3,
		totalUnits = 4,
		inProgressUnits = 2,
		centerText = "UNITS WORKED",
		showLegends = true,
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp)
	)
}
