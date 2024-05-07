package com.nextuple.nsf.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.CircularProgressBar
import com.nextuple.nsf.ui.common.HorizontalProgressBar
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.util.SubFulfillmentType

@Composable
fun MetricsScreen(
    modifier: Modifier = Modifier,
    fulfillmentType: SubFulfillmentType,
    fillRate: Float = 0f,
    productivity: Float = 0f,
    unitWorked: Int = 0,
    totalUnits: Int = 0,
    uph: Float = 0f,
    carSpeed: String? = null,
    personSpeed: String? = null
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 48.dp, vertical = 8.dp)
	) {
		Text(
			modifier = Modifier.padding(bottom = 12.dp),
			text = fulfillmentType.name,
			style = TextStyle(
				fontWeight = FontWeight(700),
				fontFamily = FontFamily.ARCHIVO,
				fontSize = 16.sp,
				lineHeight = 20.8.sp
			)
		)
		when (fulfillmentType) {
			SubFulfillmentType.BOPIS -> {
				BopisMetrics(fillRate, productivity, unitWorked, totalUnits, uph, carSpeed ?: "--:--", personSpeed ?: "--:--")
			}
			SubFulfillmentType.BOPL -> {
			}
			else -> {}
		}
	}
}

@Composable
private fun BopisMetrics(
	fillRate: Float,
	productivity: Float,
	unitWorked: Int,
	totalUnits: Int,
	uph: Float,
	carSpeed: String,
	personSpeed: String
) {
	val fillRateColor = determineColor(fillRate.toString(), MetricType.FILL_RATE, SubFulfillmentType.BOPIS)
	Row {
		Column(
			Modifier
				.padding(end = 24.dp)
				.wrapContentWidth()
		) {
			MetricsTitle(title = stringResource(R.string.fill_rate))
			CircularProgressBar(
				completedUnits = 0,
				totalUnits = 0,
				inProgressUnits = 0,
				percent = fillRate,
				centerProgressTextStyle = TextStyle(
					fontSize = 20.sp,
					fontWeight = FontWeight(700),
					lineHeight = 20.sp,
					color = fillRateColor
				),
				progressBarSize = 75.dp,
				indicatorThickness = 9.72.dp,
				completedIndicatorColor = fillRateColor
			)
		}
		val productivityColor = determineColor(
			(productivity * 100).toString(),
			MetricType.PRODUCTIVITY,
			SubFulfillmentType.BOPIS
		)
		Column {
			MetricsTitle(title = stringResource(R.string.productivity))

			Row(verticalAlignment = Alignment.CenterVertically) {
				if (productivity == 0f) {
					MetricsBody(body = "--%")
				} else {
					MetricsBody(
						body = String.format("%.1f", productivity * 100) + "%",
						color = productivityColor
					)
				}

				HorizontalProgressBar(
					modifier = Modifier.size(height = 12.dp, width = 108.dp),
					title = "",
					totalCount = null,
					workedCount = null,
					percent = productivity.toString(),
					completedColor = productivityColor
				)
			}

			Spacer(modifier = Modifier.size(8.dp))

			MetricsTitle(title = stringResource(R.string.units_worked_in_time))
			if (unitWorked == 0 && totalUnits == 0) {
				MetricsBody(body = "--/--")
			} else {
				MetricsBody(body = "$unitWorked/$totalUnits", color = determineColor("$unitWorked/$totalUnits", MetricType.UNITS_WORKED_IN_TIME, SubFulfillmentType.BOPIS))
			}
		}
	}
	HorizontalDivider(
		modifier = Modifier.padding(vertical = 8.dp),
		thickness = 1.dp,
		color = BrandColor.BORDER_DEFAULT
	)
	Row {
		Column {
			MetricsTitle(title = stringResource(R.string.bopis_pick_uph))
			if (uph == 0f) {
				MetricsBody(body = "--")
			} else {
				MetricsBody(body = uph.toString(), determineColor(uph.toString(), MetricType.UPH, SubFulfillmentType.BOPIS))
			}
		}
	}
	HorizontalDivider(
		modifier = Modifier.padding(vertical = 8.dp),
		thickness = 1.dp,
		color = BrandColor.BORDER_DEFAULT
	)
	Row {
		Column {
			MetricsTitle(title = stringResource(R.string.delivery_speed))
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(modifier = Modifier.size(32.dp), painter = painterResource(id = R.drawable.ic_car), contentDescription = "Car")
				Spacer(modifier = Modifier.size(8.dp))
				Column {
					MetricsBody(body = carSpeed, determineColor(carSpeed, MetricType.DELIVERY_CAR, SubFulfillmentType.BOPIS))
					MetricsSubscript(subscript = stringResource(R.string.mm_ss))
				}
				Spacer(modifier = Modifier.size(width = 40.dp, height = 5.dp))
				Icon(modifier = Modifier.size(36.dp), painter = painterResource(id = R.drawable.ic_store), contentDescription = "Car")
				Spacer(modifier = Modifier.size(8.dp))
				Column {
					MetricsBody(body = personSpeed, determineColor(personSpeed, MetricType.DELIVERY_PERSON, SubFulfillmentType.BOPIS))
					MetricsSubscript(subscript = stringResource(R.string.mm_ss))
				}
			}
		}
	}
	HorizontalDivider(
		modifier = Modifier.padding(vertical = 8.dp),
		thickness = 1.dp,
		color = BrandColor.BORDER_DEFAULT
	)
	Text(
		text = stringResource(R.string.updated_every_15_minutes),
		style = TextStyle(
			fontFamily = FontFamily.ARCHIVO,
			fontSize = 10.sp,
			fontStyle = FontStyle.Italic,
			fontWeight = FontWeight(400),
			lineHeight = 13.sp

		)
	)
}

@Composable
private fun MetricsTitle(title: String) {
	Text(
		modifier = Modifier.padding(bottom = 4.dp),
		text = title.uppercase(),
		style = TextStyle(
			fontFamily = FontFamily.ARCHIVO,
			fontSize = 10.sp,
			fontWeight = FontWeight(700),
			lineHeight = 11.sp
		)
	)
}

@Composable
private fun MetricsBody(body: String, color: Color = BrandColor.BLUE_300_NT) {
	Text(
		text = body,
		style = TextStyle(
			fontFamily = FontFamily.SANS,
			fontSize = 20.sp,
			fontWeight = FontWeight(700),
			lineHeight = 20.sp,
			color = color
		)
	)
}

private fun determineColor(metric: String, metricType: MetricType, fulfillmentType: SubFulfillmentType): Color {
	val color: Color = when (getLevel(metric = metric, metricType = metricType, subfulfillmentType = fulfillmentType)) {
		MetricLevel.GOOD -> BrandColor.BLUE_300_NT
		MetricLevel.MEDIUM -> BrandColor.YELLOW_400
		MetricLevel.BAD -> BrandColor.RED_600
	}

	return color
}

@Composable
private fun MetricsSubscript(subscript: String) {
	Text(
		modifier = Modifier,
		text = subscript.uppercase(),
		style = TextStyle(
			fontFamily = FontFamily.ARCHIVO,
			fontSize = 8.sp,
			fontWeight = FontWeight(700),
			lineHeight = 8.7.sp
		)
	)
}

@Composable
@PreviewPdt
fun PreviewEmptyMetricsScreen() {
	MetricsScreen(fulfillmentType = SubFulfillmentType.BOPIS)
}

@Composable
@PreviewPdt
fun PreviewMetricsScreen() {
	MetricsScreen(
		fulfillmentType = SubFulfillmentType.BOPIS,
		uph = 8.1f,
		fillRate = .973f,
		productivity = .891f,
		unitWorked = 109,
		totalUnits = 122,
		carSpeed = "02:21",
		personSpeed = "03:37"
	)
}

enum class MetricType {
	UPH, FILL_RATE, PRODUCTIVITY, UNITS_WORKED_IN_TIME, DELIVERY_CAR, DELIVERY_PERSON
}

enum class MetricLevel {
	GOOD, BAD, MEDIUM
}

fun getLevel(subfulfillmentType: SubFulfillmentType, metricType: MetricType, metric: String): MetricLevel {
	return MetricLevel.GOOD
}
