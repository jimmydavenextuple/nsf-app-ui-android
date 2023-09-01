package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackedItem
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.retrofit.dto.response.athleteFullName
import com.nextuple.nsf.ui.common.BackButton
import com.nextuple.nsf.ui.common.MultiOptionModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.SecondaryButton
import com.nextuple.nsf.ui.common.TertiaryButton
import com.nextuple.nsf.ui.common.TextInfo
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

const val YES = "Yes"
const val NO = "No"

@Composable
fun OrderDetails(
	viewState: GenericViewState = GenericViewState.Idle,
	startPickupViewState: GenericViewState = GenericViewState.Idle,
	orderDetailsResponse: OrderDetailsResponse? = null,
	onStartPickup: (String) -> Unit,
	onPickupExtend: (String) -> Unit,
	onPickupRemoveCheckIn: (String) -> Unit,
	onBackButtonClick: () -> Unit,
	onStartPickupCompletion: () -> Unit
) {
	fun enableStartPickupButton(orderDetails: OrderDetailsResponse?): Boolean {
		return !orderDetails?.fulfillmentRequestDetail?.fulfillmentRequestStatus?.code.equals("1095") // i.e. NOT PICKUP_COMPLETED
	}

	fun enableRemoveCheckInButton(orderDetails: OrderDetailsResponse?): Boolean {
		return orderDetails?.fulfillmentRequestDetail?.fulfillmentRequestStatus?.code.equals("1080") // i.e. ATHLETE_CHECKED_IN
	}

	fun enableExtendPickupButton(orderDetails: OrderDetailsResponse?): Boolean {
		return orderDetails?.athleteCheckInDetail?.pickupTaskId == null &&
			(orderDetails?.pickupExtendedCount == null || orderDetails.pickupExtendedCount == 0)
	}

	var showExtendInfoModal by remember { mutableStateOf(false) }

	fun dialogExtendPickupVisibility(visibility: Boolean) {
		showExtendInfoModal = visibility
	}

	var showRemoveCheckInInfoModal by remember { mutableStateOf(false) }

	fun dialogRemoveCheckInVisibility(visibility: Boolean) {
		showRemoveCheckInInfoModal = visibility
	}

	fun onExtendInfoModelClick(buttonText: String) {
		if (buttonText == YES) {
			orderDetailsResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber?.let {
				onPickupExtend(it)
				dialogExtendPickupVisibility(false)
			}
		} else {
			dialogExtendPickupVisibility(false)
		}
	}

	fun onRemoveCheckInInfoModelClick(buttonText: String) {
		if (buttonText == YES) {
			orderDetailsResponse?.athleteCheckInDetail?.pickupTaskId?.let {
				onPickupRemoveCheckIn(it.toString())
				dialogRemoveCheckInVisibility(false)
			}
		} else {
			dialogRemoveCheckInVisibility(false)
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding()
			.background(BrandColor.GRAY_100)
			.verticalScroll(rememberScrollState())
	) {
		BackButton(
			modifier = Modifier.padding(top = 22.dp, bottom = 10.dp),
			onBackButtonClick = onBackButtonClick
		)
		InfoCard( // Athlete Info
			modifier = Modifier
				.padding(horizontal = 24.dp, vertical = 4.dp),
			title = stringResource(id = R.string.athlete_info),
			infoList = listOf(
				Pair(
					stringResource(id = R.string.name),
					"${orderDetailsResponse?.athleteDetail?.athleteFullName()}"
				),
				Pair(
					stringResource(id = R.string.proxy_name),
					"${orderDetailsResponse?.athleteDetail?.athleteFullName()}"
				),
				Pair(
					stringResource(id = R.string.phone_number),
					"${orderDetailsResponse?.athleteDetail?.athletePhoneNumber}"
				)
			)
		)

		Spacer(modifier = Modifier.height(8.dp))
		InfoCard( // Order Info
			modifier = Modifier
				.padding(horizontal = 24.dp, vertical = 4.dp),
			title = stringResource(id = R.string.order_info),
			infoList = listOf(
				Pair(
					stringResource(id = R.string.order_number),
					orderDetailsResponse?.orderNumber
				),
				Pair(
					stringResource(id = R.string.packed_on),
					orderDetailsResponse?.packedOnDate?.let { formatTimeStamp(it) }
				),
				Pair(
					stringResource(id = R.string.holding_area),
					orderDetailsResponse?.fulfillmentRequestDetail?.holdingLocation
				),
				Pair(stringResource(id = R.string.packed_by), orderDetailsResponse?.packedByUserId)
			)
		)
		val packedItemList = orderDetailsResponse?.fulfillmentRequestDetail?.containers?.flatMap {
			it.packedItems
		}
		Spacer(modifier = Modifier.height(8.dp))
		ContentDropDown(
			modifier = Modifier
				.padding(horizontal = 24.dp, vertical = 4.dp),
			packedItemList = packedItemList
		)

		Spacer(modifier = Modifier.height(8.dp))
		PickupActionsCard(
			modifier = Modifier
				.padding(horizontal = 24.dp, vertical = 4.dp)
				.fillMaxWidth(),
			onStartPickup = {
				orderDetailsResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber?.let {
					onStartPickup(it)
				}
			},
			enableStartPickupButton = enableStartPickupButton(orderDetailsResponse),
			enableRemoveCheckInButton = enableRemoveCheckInButton(orderDetailsResponse),
			enableExtendPickupButton = enableExtendPickupButton(orderDetailsResponse),
			onPickupExtendClicked = {
				dialogExtendPickupVisibility(true)
			},
			onPickupRemoveCheckInClicked = {
				dialogRemoveCheckInVisibility(true)
			}
		)

		Spacer(modifier = Modifier.height(8.dp))
		OrderManagementCard(
			modifier = Modifier
				.padding(horizontal = 24.dp, vertical = 4.dp)
				.fillMaxWidth()
		)
	}
	if (viewState == GenericViewState.Loading || startPickupViewState == GenericViewState.Loading) {
		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				color = BrandColor.GRAY_900
			)
		}
	}
	if (startPickupViewState == GenericViewState.Success) {
		onStartPickupCompletion()
	}
	if (showExtendInfoModal) {
		MultiOptionModal(
			title = stringResource(id = R.string.info_modal_order_extend_pickup_title),
			subTitle = stringResource(id = R.string.info_modal_order_extend_pickup_message),
			buttons = listOf(YES, NO),
			buttonClick = { buttonText ->
				onExtendInfoModelClick(buttonText)
			},
			crossIconClick = { dialogExtendPickupVisibility(false) },
			onDismissRequest = { dialogExtendPickupVisibility(false) }
		)
	}

	if (showRemoveCheckInInfoModal) {
		MultiOptionModal(
			title = stringResource(id = R.string.info_modal_order_remove_check_in_title),
			subTitle = stringResource(id = R.string.info_modal_order_remove_check_in_message),
			buttons = listOf(YES, NO),
			buttonClick = { buttonText ->
				onRemoveCheckInInfoModelClick(buttonText)
			},
			crossIconClick = { dialogRemoveCheckInVisibility(false) },
			onDismissRequest = { dialogRemoveCheckInVisibility(false) }
		)
	}
}

@Composable
private fun InfoCard(
	modifier: Modifier = Modifier,
	title: String,
	infoList: List<Pair<String, String?>>
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(
				text = title,
				fontFamily = FontFamily.ARCHIVO,
				fontSize = 20.sp,
				fontWeight = FontWeight.Bold,
				letterSpacing = 0.5.sp
			)

			Spacer(modifier = Modifier.height(8.dp))

			Row {
				TextInfo(
					modifier = Modifier.weight(0.5f),
					label = infoList[0].first,
					value = infoList[0].second ?: ""
				)
				TextInfo(
					modifier = Modifier.weight(0.5f),
					label = infoList[1].first,
					value = infoList[1].second ?: ""
				)
			}

			Spacer(modifier = Modifier.height(8.dp))

			Row {
				TextInfo(
					modifier = Modifier.weight(0.5f),
					label = infoList[2].first,
					value = infoList[2].second ?: ""
				)
				if (infoList.size == 4) {
					TextInfo(
						modifier = Modifier.weight(0.5f),
						label = infoList[3].first,
						value = infoList[3].second ?: ""
					)
				}
			}
		}
	}
}

@Composable
private fun ContentDropDown(
	modifier: Modifier = Modifier,
	packedItemList: List<PackedItem>?
) {
	var isExpanded by remember {
		mutableStateOf(false)
	}

	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(id = R.string.contents),
					fontFamily = FontFamily.ARCHIVO,
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold,
					letterSpacing = 0.5.sp
				)
				Spacer(
					modifier = Modifier
						.weight(1f)
				)
				TertiaryButton(
					text = if (isExpanded) {
						stringResource(id = R.string.hide)
					} else {
						stringResource(id = R.string.show)
					},
					onButtonClick = {
						isExpanded = !isExpanded
					},
					tag = "showHideBtn"
				)
				Icon(
					modifier = Modifier
						.padding(start = 5.dp)
						.clickable { isExpanded = !isExpanded },
					painter = if (isExpanded) {
						painterResource(id = R.drawable.ic_arrow_up)
					} else {
						painterResource(id = R.drawable.ic_arrow_drop_down)
					},
					contentDescription = "back button"
				)
			}

			if (isExpanded) {
				Spacer(modifier = Modifier.height(5.dp))
				packedItemList?.forEach {
					PackedItemCard(
						Modifier
							.padding(horizontal = 12.dp, vertical = 4.dp)
							.background(BrandColor.GRAY_100)
							.fillMaxWidth(),
						packedItem = it
					)
				}
			}
		}
	}
}

@Composable
fun PackedItemCard(modifier: Modifier = Modifier, packedItem: PackedItem) {
	Row(
		modifier = modifier
	) {
		AsyncImage(
			model = packedItem.productImageUrls.firstOrNull(),
			error = painterResource(id = R.drawable.placeholder_image),
			placeholder = painterResource(id = R.drawable.placeholder_image),
			contentDescription = null,
			modifier = Modifier
				.padding(start = 15.dp, top = 9.dp, end = 23.dp, bottom = 15.dp)
				.size(64.dp)
		)

		Column(modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)) {
			Text(
				text = packedItem.productName,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				fontSize = 14.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight.Bold,
				letterSpacing = 0.5.sp
			)

			packedItem.primaryAttr?.value?.let {
				OrderTextInfo(
					label = packedItem.primaryAttr.name,
					value = it
				)
			}
			packedItem.secondaryAttr?.value?.let {
				OrderTextInfo(
					label = packedItem.secondaryAttr.name,
					value = it
				)
			}
			OrderTextInfo(
				label = stringResource(id = R.string.upc),
				value = packedItem.scannedUpc ?: ""
			)
		}
	}
}

@Composable
private fun OrderTextInfo(label: String, value: String) {
	Text(
		text = "$label: $value",
		maxLines = 1,
		overflow = TextOverflow.Ellipsis,
		fontSize = 12.sp,
		fontFamily = FontFamily.ARCHIVO,
		fontWeight = FontWeight.Normal,
		letterSpacing = 0.5.sp
	)
}

@Composable
private fun PickupActionsCard(
	modifier: Modifier = Modifier,
	onStartPickup: () -> Unit,
	enableStartPickupButton: Boolean,
	enableExtendPickupButton: Boolean,
	enableRemoveCheckInButton: Boolean,
	onPickupExtendClicked: () -> Unit,
	onPickupRemoveCheckInClicked: () -> Unit
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(
			modifier = Modifier
				.padding(12.dp)
				.fillMaxWidth(),
			horizontalAlignment = Alignment.Start
		) {
			Text(
				modifier = Modifier.align(Alignment.Start),
				text = stringResource(id = R.string.pickup_actions),
				fontFamily = FontFamily.ARCHIVO,
				fontSize = 20.sp,
				fontWeight = FontWeight.Bold,
				letterSpacing = 0.5.sp
			)
			Spacer(modifier = Modifier.height(8.dp))
			PrimaryButton(
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.fillMaxWidth(0.8f),
				text = stringResource(id = R.string.start_pickup),
				enabled = enableStartPickupButton,
				onButtonClick = { onStartPickup() }
			)
			Spacer(modifier = Modifier.height(8.dp))
			SecondaryButton(
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.fillMaxWidth(0.8f),
				text = stringResource(id = R.string.extend_pickup),
				enabled = enableExtendPickupButton,
				onButtonClick = {
					onPickupExtendClicked()
				}
			)
			Spacer(modifier = Modifier.height(8.dp))
			SecondaryButton(
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.fillMaxWidth(0.8f),
				text = stringResource(id = R.string.remove_check_in),
				enabled = enableRemoveCheckInButton,
				onButtonClick = {
					onPickupRemoveCheckInClicked()
				}
			)
			Spacer(modifier = Modifier.height(12.dp))
		}
	}
}

@Composable
private fun OrderManagementCard(modifier: Modifier = Modifier) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(
			modifier = Modifier
				.padding(12.dp)
				.fillMaxWidth(),
			horizontalAlignment = Alignment.Start
		) {
			Text(
				modifier = Modifier.align(Alignment.Start),
				text = stringResource(id = R.string.order_management),
				fontFamily = FontFamily.ARCHIVO,
				fontSize = 20.sp,
				fontWeight = FontWeight.Bold,
				letterSpacing = 0.5.sp
			)
			Spacer(modifier = Modifier.height(8.dp))
			SecondaryButton(
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.fillMaxWidth(0.8f),
				text = stringResource(id = R.string.print_hold_slip),
				onButtonClick = {}
			)
			Spacer(modifier = Modifier.height(12.dp))
		}
	}
}

fun formatTimeStamp(
	timeStamp: String,
	inputPattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'",
	outputPattern: String = "MMMM d, yyyy"
): String {
	val inputFormatter = DateTimeFormatter.ofPattern(inputPattern, Locale.ENGLISH)
	val outputFormatter = DateTimeFormatter.ofPattern(outputPattern, Locale.ENGLISH)

	val dateTime = LocalDateTime.parse(timeStamp, inputFormatter)
	return dateTime.format(outputFormatter)
}

@PreviewPdt
@Composable
fun OrderDetailsPreview() {
	OrderDetails(
		viewState = GenericViewState.Success,
		startPickupViewState = GenericViewState.Idle,
		onBackButtonClick = {},
		onStartPickup = {},
		onPickupExtend = {},
		onPickupRemoveCheckIn = {},
		onStartPickupCompletion = {}
	)
}
