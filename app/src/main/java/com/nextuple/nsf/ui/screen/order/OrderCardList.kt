package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.retrofit.dto.response.athleteShortName
import com.nextuple.nsf.retrofit.dto.response.sddShortName
import com.nextuple.nsf.util.OrderStatus

@Composable
fun OrderCardList(
	modifier: Modifier = Modifier,
	orderList: List<OrderDetailsResponse>?,
	sddReadyList: List<List<OrderDetailsResponse>>?,
	getOrderDetails: (String) -> Unit
) {
	val flatSddList: List<OrderDetailsResponse> = sddReadyList?.flatten() ?: emptyList()
	LazyColumn(modifier = modifier) {
		if (!sddReadyList.isNullOrEmpty()) {
			items(sddReadyList) {
				SDDOrderCard(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 4.dp),
					listOfOrdersInBatch = it,
					onClick = {
					}
				)
			}
		}
		if (!orderList.isNullOrEmpty()) {
			items(orderList) {
				if (!flatSddList.contains(it)) {
					OrderCard(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp, vertical = 4.dp),
						orderType = it.fulfillmentRequestDetail.subFulfillmentType.orEmpty(),
						athleteShortName = it.athleteDetail?.athleteShortName().orEmpty(),
						athleteSddShortName = it.athleteDetail?.sddShortName().orEmpty(),
						orderNo = it.orderNumber.orEmpty(),
						athleteLocation = it.athleteCheckInDetail?.athleteLocation.orEmpty(),
						holdingLocation = it.fulfillmentRequestDetail.holdingLocation.orEmpty(),
						orderStatus = OrderStatus.getByStatus(it.orderStatusText.orEmpty()),
						onClick = {
							it.fulfillmentRequestDetail.fulfillmentRequestNumber.let { fulfillmentRequestNumber ->
								getOrderDetails(
									fulfillmentRequestNumber
								)
							}
						}
					)
				}
			}
		}
	}
}
