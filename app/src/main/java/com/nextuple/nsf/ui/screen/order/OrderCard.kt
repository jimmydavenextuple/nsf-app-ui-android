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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nextuple.nsf.R
import com.nextuple.nsf.R.string
import com.nextuple.nsf.ui.common.ScanIcon
import com.nextuple.nsf.ui.common.chip.OrderStatusChip
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.util.OrderStatus
import com.nextuple.nsf.util.OrderStatus.AGED
import com.nextuple.nsf.util.OrderStatus.CANCELED
import com.nextuple.nsf.util.OrderStatus.CHECKED_IN
import com.nextuple.nsf.util.OrderStatus.DEFAULT
import com.nextuple.nsf.util.OrderStatus.IN_PROGRESS
import com.nextuple.nsf.util.SubFulfillmentType

@Composable
fun OrderCard(
	modifier: Modifier = Modifier,
	orderType: String,
	athleteShortName: String,
	athleteSddShortName: String? = null,
	athleteLocation: String,
	holdingLocation: String,
	orderStatus: OrderStatus,
	imageList: List<String> = emptyList(),
	isScanned: Boolean? = null,
	onClick: () -> Unit
) {
	val isReadyOrder = OrderStatus.isReadyStatusText(orderStatus.statusText)
	if (orderType == SubFulfillmentType.SAME_DAY.subFulfillmentTypeName && (orderStatus == CHECKED_IN || orderStatus == DEFAULT)) {
		AthleteDetail(
			orderType = orderType,
			athleteShortName = athleteShortName,
			athleteSddShortName = athleteSddShortName,
			athleteLocation = athleteLocation,
			holdingLocation = holdingLocation,
			orderStatus = orderStatus,
			indicatorSize = 4,
			isReadyOrder = true,
			imageList = imageList,
			isScanned = isScanned,
			onClick = {}
		)
	} else {
		Card(
			modifier = modifier.wrapContentHeight(),
			shape = RoundedCornerShape(12.dp),
			onClick = onClick,
			colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50),
			elevation = CardDefaults.cardElevation(3.dp)
		) {
			AthleteDetail(
				orderType = orderType,
				athleteShortName = athleteShortName,
				athleteLocation = athleteLocation,
				holdingLocation = holdingLocation,
				orderStatus = orderStatus,
				isReadyOrder = isReadyOrder,
				onClick = onClick
			)
		}
	}
}

@Composable
fun AthleteDetail(
	modifier: Modifier = Modifier,
	orderType: String,
	athleteShortName: String,
	athleteSddShortName: String? = null,
	athleteLocation: String,
	holdingLocation: String,
	orderStatus: OrderStatus?,
	isReadyOrder: Boolean,
	indicatorSize: Int = 8,
	imageList: List<String> = emptyList(),
	isScanned: Boolean? = null,
	onClick: () -> Unit
) {
	Row(
		modifier = Modifier
			.background(color = BrandColor.WHITE)
			.fillMaxWidth()
			.height(if (isReadyOrder) 100.dp else 90.dp)
			.padding(12.dp),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			StatusIndicator(orderStatus ?: CHECKED_IN, indicatorSize)

			Column(
				modifier = Modifier.fillMaxHeight(),
				verticalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = athleteShortName,
					fontSize = 20.sp,
					fontWeight = FontWeight(700),
					color = BrandColor.GRAY_900,
					letterSpacing = 0.5.sp
				)

				// TODO: Add else with teammate name once available in API responses.
				if (isReadyOrder) {
					// commenting out. Testing view with holding location and bin on same line
					// val (area, bin) = StringUtils.splitAreaAndBin(holdingLocation)
					// HoldingLocation(area = area, bin = bin)
					HoldingLocation(area = holdingLocation, bin = "")
				}
			}
		}

		Column(
			modifier = Modifier
				.fillMaxHeight()
				.padding(end = 6.dp),
			verticalArrangement = Arrangement.SpaceBetween,
			horizontalAlignment = Alignment.End
		) {
			Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
				if (!athleteSddShortName.isNullOrEmpty()) {
					// SDD ALT Name
					Text(
						text = "ALT: $athleteSddShortName",
						style = TextStyle(
							fontSize = 10.sp,
							fontWeight = FontWeight(700),
							color = BrandColor.GRAY_600,
							textAlign = TextAlign.Right,
							letterSpacing = 1.5.sp
						)
					)
				} else {
					OrderStatusChip(
						modifier = Modifier.align(Alignment.End),
						statusText = orderStatus?.statusText ?: ""
					)
				}

				val isCheckedIn = orderStatus == CHECKED_IN
				val shouldDisplayAthleteLocation = isCheckedIn && athleteLocation.isNotEmpty()

				if (shouldDisplayAthleteLocation) {
					OrderStatusChip(
						modifier = Modifier.align(Alignment.End),
						statusText = athleteLocation
					)
				}
			}
			if (athleteSddShortName.isNullOrEmpty()) {
				Text(
					modifier = Modifier
						.padding(end = 8.dp)
						.align(Alignment.End),
					text = orderType,
					fontSize = 12.sp,
					fontWeight = FontWeight(700),
					color = BrandColor.GRAY_600,
					letterSpacing = 1.5.sp
				)
			} else {
				LazyRow(
					modifier = Modifier.padding(bottom = 4.dp),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					// Only showing at most 2 images for an order
					items(imageList.subList(0, minOf(imageList.size, 2))) { image ->
						AsyncImage(
							modifier = Modifier.size(40.dp),
							model = image,
							error = painterResource(id = R.drawable.placeholder_image),
							placeholder = painterResource(id = R.drawable.placeholder_image),
							contentDescription = null,
							contentScale = ContentScale.FillHeight
						)
					}
					isScanned?.let {
						item {
							ScanIcon(
								modifier = Modifier
									.size(height = 30.dp, width = 34.dp),
								isScanned = false // todo: handle scan value
							)
						}
					}
				}
			}
		}
	}
}

@Composable
fun StatusIndicator(orderStatus: OrderStatus, width: Int) {
	val indicatorColor = when (orderStatus) {
		CHECKED_IN,
		IN_PROGRESS,
		AGED,
		CANCELED -> orderStatus.statusColor

		else -> null
	}
	indicatorColor?.let {
		Box(
			modifier = Modifier
				.width(width.dp)
				.fillMaxHeight()
				.background(
					color = indicatorColor,
					shape = RoundedCornerShape(size = 4.dp)
				)
		)
	}
}

@Composable
fun HoldingLocation(modifier: Modifier = Modifier, area: String, bin: String) {
	Column(modifier = modifier) {
		Text(
			text = stringResource(id = string.location),
			fontSize = 12.sp,
			fontWeight = FontWeight(700),
			color = BrandColor.BLACK,
			letterSpacing = 1.5.sp
		)
		Text(
			text = area,
			fontSize = 14.sp,
			fontWeight = FontWeight(400),
			color = BrandColor.BLACK,
			letterSpacing = 0.5.sp
		)
		if (bin.isNotEmpty()) {
			Text(
				text = bin,
				fontSize = 14.sp,
				fontWeight = FontWeight(400),
				color = BrandColor.BLACK,
				letterSpacing = 0.5.sp
			)
		}
	}
}

@Composable
@Preview
private fun PreviewOrderCard() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = "BOPIS",
		athleteShortName = "Litt, L.",
		athleteLocation = "Spot #2",
		holdingLocation = "Main Holding Area Bin 2",
		athleteSddShortName = null,
		orderStatus = OrderStatus.CHECKED_IN
	) {}
}

@Composable
@Preview
private fun PreviewOrderCardAged() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = SubFulfillmentType.BOPIS.subFulfillmentTypeName,
		athleteShortName = "Litt, L.",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2",
		orderStatus = OrderStatus.AGED
	) {}
}

@Composable
@Preview
private fun PreviewOrderCardReadySdd() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = SubFulfillmentType.SAME_DAY.subFulfillmentTypeName,
		athleteShortName = "Litt, L.",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2",
		orderStatus = OrderStatus.READY
	) {}
}

@Composable
@Preview
private fun PreviewOrderCardReady() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = SubFulfillmentType.BOPIS.subFulfillmentTypeName,
		athleteShortName = "Litt, L.",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2",
		orderStatus = OrderStatus.READY
	) {}
}

@Composable
@Preview
private fun PreviewOrderCardInProgress() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = SubFulfillmentType.BOPIS.subFulfillmentTypeName,
		athleteShortName = "Litt, L.",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2",
		orderStatus = OrderStatus.BEING_PACKED
	) {}
}

@Composable
@Preview
private fun PreviewSDDOrderCard() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = SubFulfillmentType.SAME_DAY.subFulfillmentTypeName,
		athleteShortName = "Litt, C.",
		athleteSddShortName = "Chicken L.",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2",
		orderStatus = CHECKED_IN
	) {}
}

@Composable
@Preview
private fun PreviewSDDOrderPickupCard() {
	OrderCard(
		modifier = Modifier.fillMaxWidth(),
		orderType = SubFulfillmentType.SAME_DAY.subFulfillmentTypeName,
		athleteShortName = "Litt, C.",
		athleteSddShortName = "Chicken L.",
		athleteLocation = "",
		holdingLocation = "Main Holding Area Bin 2",
		imageList = listOf("asdf", "asdf", "asdf"),
		orderStatus = DEFAULT
	) {}
}
