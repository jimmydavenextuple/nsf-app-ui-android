package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R.string
import com.nextuple.nsf.ui.common.chip.OrderStatusChip
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.util.OrderStatus
import com.nextuple.nsf.util.OrderStatus.AGED
import com.nextuple.nsf.util.OrderStatus.CANCELED
import com.nextuple.nsf.util.OrderStatus.CHECKED_IN
import com.nextuple.nsf.util.OrderStatus.IN_PROGRESS
import com.nextuple.nsf.util.StringUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(
	modifier: Modifier = Modifier,
	orderType: String,
	athleteShortName: String,
	orderStatus: String,
	athleteLocation: String,
	holdingLocation: String,
	onClick: () -> Unit
) {
	val resolvedStatus = OrderStatus.getByStatus(orderStatus)
	val isReadyOrder = OrderStatus.isReadyStatusText(orderStatus)

	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		onClick = onClick,
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_100),
		elevation = CardDefaults.cardElevation(3.dp)
	) {
		Row(
			modifier = Modifier
				.background(color = BrandColor.WHITE)
				.fillMaxWidth()
				.height(if (isReadyOrder) 116.dp else 78.dp)
				.padding(12.dp),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				val indicatorColor = when (resolvedStatus) {
					CHECKED_IN,
					IN_PROGRESS,
					AGED,
					CANCELED -> resolvedStatus.statusColor

					else -> null
				}
				indicatorColor?.let {
					Box(
						modifier = Modifier
							.width(8.dp)
							.fillMaxHeight()
							.background(
								color = indicatorColor,
								shape = RoundedCornerShape(size = 4.dp)
							)
					)
				}

				Column(
					modifier = Modifier.fillMaxHeight(),
					verticalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = athleteShortName,
						fontSize = 20.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(700),
						color = BrandColor.GRAY_900,
						letterSpacing = 0.5.sp
					)

					// TODO: Add else with teammate name once available in API responses.
					if (isReadyOrder) {
						val (area, bin) = StringUtils.splitAreaAndBin(holdingLocation)
						HoldingLocation(area = area, bin = bin)
					}
				}
			}

			Column(
				modifier = Modifier.fillMaxHeight(),
				verticalArrangement = Arrangement.SpaceBetween
			) {
				Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
					OrderStatusChip(
						modifier = Modifier.align(Alignment.End),
						statusText = orderStatus
					)

					val isCheckedIn = OrderStatus.getByStatus(orderStatus) == CHECKED_IN
					val shouldDisplayAthleteLocation = isCheckedIn && athleteLocation.isNotEmpty()

					if (shouldDisplayAthleteLocation) {
						OrderStatusChip(
							modifier = Modifier.align(Alignment.End),
							statusText = athleteLocation
						)
					}
				}

				Text(
					modifier = Modifier
						.padding(end = 8.dp)
						.align(Alignment.End),
					text = orderType,
					fontSize = 12.sp,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight(700),
					color = BrandColor.GRAY_600,
					letterSpacing = 1.5.sp
				)
			}
		}
	}
}

@Composable
private fun HoldingLocation(modifier: Modifier = Modifier, area: String, bin: String) {
	Column(modifier = modifier) {
		Text(
			text = stringResource(id = string.location),
			fontSize = 12.sp,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight(700),
			color = BrandColor.BLACK,
			letterSpacing = 1.5.sp
		)
		Text(
			text = area,
			fontSize = 14.sp,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight(400),
			color = BrandColor.BLACK,
			letterSpacing = 0.5.sp
		)
		if (bin.isNotEmpty()) {
			Text(
				text = bin,
				fontSize = 14.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight(400),
				color = BrandColor.BLACK,
				letterSpacing = 0.5.sp
			)
		}
	}
}

@Composable
@Preview
fun PreviewOrderCard() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = "BOPIS",
		athleteShortName = "Litt, L.",
		orderStatus = "Checked In",
		athleteLocation = "Spot #2",
		holdingLocation = "Main Holding Area Bin 2"
	) {}
}

@Composable
@Preview
fun PreviewOrderCardAged() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = "BOPIS",
		athleteShortName = "Litt, L.",
		orderStatus = "Aged",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2"
	) {}
}

@Composable
@Preview
fun PreviewOrderCardReady() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = "BOPIS",
		athleteShortName = "Litt, L.",
		orderStatus = "Ready",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2"
	) {}
}

@Composable
@Preview
fun PreviewOrderCardInProgress() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = "BOPIS",
		athleteShortName = "Litt, L.",
		orderStatus = "Being Packed",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2"
	) {}
}
