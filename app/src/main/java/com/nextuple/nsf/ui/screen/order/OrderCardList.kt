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


@Composable
fun OrderCardList(
	modifier: Modifier = Modifier,
	orderList: List<OrderDetailsResponse>,
	getOrderDetails: (String) -> Unit
) {
	LazyColumn(modifier = modifier) {
		items(orderList) {
			OrderCard(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 4.dp),
				orderType = it.fulfillmentRequestDetail.subFulfillmentType.orEmpty(),
				athleteShortName = it.athleteDetail?.athleteShortName().orEmpty(),
				orderStatus = it.orderStatusText.orEmpty(),
				athleteLocation = it.athleteCheckInDetail?.athleteLocation.orEmpty(),
				holdingLocation = it.fulfillmentRequestDetail.holdingLocation.orEmpty(),
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
