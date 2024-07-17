package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.AthleteCheckInDetail
import com.nextuple.nsf.retrofit.dto.response.AthleteDetail
import com.nextuple.nsf.retrofit.dto.response.DriverDetail
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.retrofit.dto.response.athleteShortName
import com.nextuple.nsf.retrofit.dto.response.sddShortName
import com.nextuple.nsf.ui.common.chip.OrderStatusChip
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.util.OrderStatus

@Composable
fun SDDOrderCard(
	modifier: Modifier = Modifier,
	listOfOrdersInBatch: List<OrderDetailsResponse>,
	onClick: () -> Unit
) {
	val orderStatus = OrderStatus.CHECKED_IN

	Card(
		modifier = modifier.wrapContentHeight(),
		shape = RoundedCornerShape(12.dp),
		onClick = onClick,
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50),
		elevation = CardDefaults.cardElevation(3.dp)
	) {
		Column(
			Modifier
				.padding(start = 12.dp, top = 8.dp)
		) {
			Row(Modifier.heightIn(min = 60.dp, max = 80.dp)) {
				StatusIndicator(orderStatus = orderStatus, 8)
				Column {
					Row {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Image(
								modifier = Modifier
									.padding(horizontal = 8.dp)
									.size(24.dp),
								painter = painterResource(id = R.drawable.ic_doordash_logo),
								contentDescription = "doordash logo"
							)
							Text(
								text = listOfOrdersInBatch[0].driverDetail?.sddShortName()
									.orEmpty(),
								style = TextStyle(
									fontSize = 18.sp,
									lineHeight = 23.4.sp,
									fontWeight = FontWeight(700),
									color = BrandColor.BLACK,

									letterSpacing = 0.5.sp
								)
							)
						}
						Row(modifier = Modifier, verticalAlignment = Alignment.Top) {
							Column(
								Modifier
									.fillMaxWidth()
									.padding(end = 16.dp)
							) {
								OrderStatusChip(
									modifier = Modifier.align(Alignment.End),
									statusText = orderStatus.statusText
								)
								Spacer(modifier = Modifier.size(2.dp))
								if (!listOfOrdersInBatch[0].athleteCheckInDetail?.athleteLocation.isNullOrEmpty()) {
									listOfOrdersInBatch[0].athleteCheckInDetail?.athleteLocation?.let {
										OrderStatusChip(
											modifier = Modifier.align(Alignment.End),
											statusText = it
										)
									}
								}
							}
						}
					}

					Column(Modifier.padding(start = 8.dp, top = 4.dp)) {
						Text(
							text = stringResource(R.string.sdd_pickup),
							style = TextStyle(
								fontSize = 12.sp,
								fontWeight = FontWeight(700),
								color = BrandColor.BLACK,
								letterSpacing = 1.5.sp
							)
						)

						Text(
							text = "${listOfOrdersInBatch.size} order(s)",
							style = TextStyle(
								fontSize = 12.sp,
								lineHeight = 15.6.sp,
								fontWeight = FontWeight(400),
								color = BrandColor.BLACK,
								letterSpacing = 0.5.sp
							)
						)
					}
				}
			}
			LazyColumn(
				Modifier
					.padding(start = 8.dp)
					.heightIn(min = 80.dp, max = 800.dp)
			) {
				items(listOfOrdersInBatch) {
					if (0 != listOfOrdersInBatch.indexOf(it)) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.Center
						) {
							HorizontalDivider(
								Modifier
									.fillMaxWidth(.87f)
									.height(1.dp)
									.background(color = BrandColor.GRAY_350)
							)
						}
					}
					OrderCard(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp, vertical = 4.dp),
						orderType = it.fulfillmentRequestDetail.subFulfillmentType.orEmpty(),
						athleteShortName = it.athleteDetail?.athleteShortName().orEmpty(),
						athleteSddShortName = it.athleteDetail?.sddShortName()?.uppercase()
							.orEmpty(),
						orderNo = it.orderNumber?.uppercase()
							.orEmpty(),
						athleteLocation = "",
						holdingLocation = it.fulfillmentRequestDetail.holdingLocation.orEmpty(),
						orderStatus = orderStatus,
						// TODO: fix on click
						onClick = {}
					)
				}
			}
		}
	}
}

@Composable
@Preview
private fun PreviewSDDCheckedInOrderCard() {
	SDDOrderCard(
		modifier = Modifier.fillMaxWidth(),
		listOfOrdersInBatch = listOf(
			OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "100000000123.001",
					fulfillmentType = "SFS",
					subFulfillmentType = "SAME DAY",
					holdingLocation = "Main Holding Area Bin 14"
				),
				driverDetail = DriverDetail(
					"Missy",
					"Elliot",
					"4"
				),
				athleteDetail = AthleteDetail(
					athleteFirstName = "Beyonce",
					athleteLastName = "Carter"
				),
				athleteCheckInDetail = AthleteCheckInDetail(
					athleteLocation = "InStore"
				)
			)
		)
	) {}
}

@Composable
@Preview
private fun PreviewMultiSDDCheckedInOrderCard() {
	SDDOrderCard(
		modifier = Modifier.fillMaxWidth(),
		listOfOrdersInBatch = listOf(
			OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "100000000123.001",
					fulfillmentType = "SFS",
					subFulfillmentType = "SAME_DAY",
					holdingLocation = "Main Holding Area Bin 14"
				),
				driverDetail = DriverDetail(
					"Missy",
					"Elliot",
					"4"
				),
				athleteDetail = AthleteDetail(
					athleteFirstName = "Beyonce",
					athleteLastName = "Carter"
				)
			),
			OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "100000000124.001",
					fulfillmentType = "SFS",
					subFulfillmentType = "SAME_DAY",
					holdingLocation = "Main Holding Area Bin 14"
				),
				driverDetail = DriverDetail(
					"Missy",
					"Elliot",
					"4"
				),
				athleteDetail = AthleteDetail(
					athleteFirstName = "Jay",
					athleteLastName = "Zed"
				)
			)
		)
	) {}
}
