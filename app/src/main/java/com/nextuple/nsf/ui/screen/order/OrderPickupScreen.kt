package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.retrofit.dto.response.athleteFullName
import com.nextuple.nsf.retrofit.dto.response.athleteProxyFullName
import com.nextuple.nsf.ui.common.BackButton
import com.nextuple.nsf.ui.common.CallToAction
import com.nextuple.nsf.ui.common.CallToActionMode
import com.nextuple.nsf.ui.common.ImageList
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.TextInfo
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt

const val STEP_GET_ORDER: Int = 1
const val STEP_BRING_TO_ATHLETE: Int = 2

@Composable
fun PickOrderScreen(
	completeOrderPickupState: GenericViewState = GenericViewState.Idle,
	orderDetails: OrderDetailsResponse?,
	onBackButtonClick: () -> Unit,
	defaultStep: Int = STEP_GET_ORDER,
	onOrderPickupClicked: (String) -> Unit,
	onOrderPickupSuccess: () -> Unit
) {
	val currentStep = remember {
		mutableStateOf(defaultStep)
	}
	var showInfoModal by remember { mutableStateOf(false) }

	fun showCompleteOrderPickInfoModal(visibility: Boolean) {
		showInfoModal = visibility
	}

	fun isOrderActive(): Boolean = currentStep.value == STEP_GET_ORDER
	fun isAthleteActive(): Boolean = currentStep.value == STEP_BRING_TO_ATHLETE
	LaunchedEffect(Unit) {
		currentStep.value = defaultStep
	}

	Box(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding()
				.background(BrandColor.GRAY_100)
				.verticalScroll(rememberScrollState())
		) {


			//Header
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.background(color = BrandColor.GRAY_200)
					.padding(start = 20.dp)
					.height(40.dp)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					BackButton(
						modifier = Modifier.fillMaxHeight().padding(top = 10.dp, bottom = 10.dp),
						onBackButtonClick = onBackButtonClick
					)

				}
			}

			OrderPickupCardItem(
				stepNumber = if (isOrderActive()) "1" else null,
				title = stringResource(id = R.string.get_order_title),
				subTitle = stringResource(id = R.string.get_order_description),
				isActive = isOrderActive(),
				extraContent = {
					GetOrdersContent(orderDetails) {
						currentStep.value = STEP_BRING_TO_ATHLETE
					}
				}
			)
			OrderPickupCardItem(
				stepNumber = "2",
				title = stringResource(id = R.string.bring_to_customer_title),
				subTitle = stringResource(id = R.string.bring_to_athlete_description),
				isActive = isAthleteActive(),
				extraContent = { BringToAthleteContent(orderDetails, onOrderPickupClicked) }
			)

			if (showInfoModal) {
				InfoModal(
					modifier = Modifier.fillMaxWidth(0.95f),
					title = stringResource(id = R.string.info_modal_order_picked_up_title),
					subTitle = stringResource(id = R.string.info_modal_order_picked_up_message),
					buttonText = stringResource(id = R.string.ok),
					buttonClick = {
						showCompleteOrderPickInfoModal(false)
						onOrderPickupSuccess()
					},
					crossIconClick = {
						showCompleteOrderPickInfoModal(false)
						onOrderPickupSuccess()
					},
					dismissOnBackPress = false,
					dismissOnClickOutside = false,
					onDismissRequest = { }
				)
			}
		}
		if (completeOrderPickupState == GenericViewState.Loading) {
			CircularProgressIndicator(
				modifier = Modifier
					.wrapContentSize()
					.align(Alignment.Center),
				color = BrandColor.GRAY_900
			)
		} else if (completeOrderPickupState == GenericViewState.Success) {
			showCompleteOrderPickInfoModal(true)
		}
	}
}

@Composable
private fun OrderPickupCardItem(
	stepNumber: String?,
	title: String,
	subTitle: String,
	isActive: Boolean,
	extraContent: @Composable () -> Unit = {}

) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.padding(horizontal = 24.dp, vertical = 4.dp),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				StepNumberWithCircle(
					text = stepNumber,
					circleColor = stepCountCircleColor(isActive),
					textColor = BrandColor.GRAY_50
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = title,
					fontFamily = FontFamily.ARCHIVO,
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold,
					letterSpacing = 0.5.sp,
					color = cardTitleTextColor(isActive)
				)
			}
			if (isActive) {
				Spacer(modifier = Modifier.height(12.dp))
				Text(
					text = subTitle,
					fontFamily = FontFamily.ARCHIVO,
					fontSize = 16.sp,
					fontWeight = FontWeight.Normal,
					letterSpacing = 0.5.sp,
					color = BrandColor.BLUE_800_NT
				)
				Spacer(modifier = Modifier.height(8.dp))
				extraContent()
			}
		}
	}
}

@Composable
private fun GetOrdersContent(orderDetails: OrderDetailsResponse? = null, onScanClicked: () -> Unit) {
	Row {
		TextInfo(
			modifier = Modifier.weight(0.5f),
			label = stringResource(id = R.string.customer_name),
			value = orderDetails?.athleteDetail?.athleteFullName()
		)
		TextInfo(
			modifier = Modifier.weight(0.5f),
			label = stringResource(id = R.string.phone_number),
			value = orderDetails?.athleteDetail?.athletePhoneNumber
		)
	}

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.padding(top = 8.dp, bottom = 8.dp),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_100)
	) {
		Row {
			Column(
				modifier = Modifier
					.weight(0.5f)
					.padding(12.dp)
					.fillMaxWidth()
			) {
				TextInfo(
					modifier = Modifier.fillMaxWidth(),
					label = stringResource(id = R.string.holding_area),
					value = orderDetails?.fulfillmentRequestDetail?.holdingLocation
				)
				Spacer(modifier = Modifier.height(8.dp))
				val imageList =
					orderDetails?.fulfillmentRequestDetail?.containers?.firstOrNull()?.packedItems?.map {
						it.productImageUrls.firstOrNull() ?: ""
					}?.filter {
						it.isNotEmpty()
					}
				ImageList(
					images = imageList ?: emptyList()
				)
			}
			Column(
				modifier = Modifier
					.weight(0.5f)
					.padding(12.dp)
					.fillMaxWidth()
					.align(Alignment.CenterVertically),
				verticalArrangement = Arrangement.Center
			) {
				CallToAction(
					modifier = Modifier
						.size(64.dp)
						.align(Alignment.CenterHorizontally),
					callToActionMode = CallToActionMode.Scan(),

					onClick = {
						onScanClicked()
					}
				)
				Spacer(modifier = Modifier.height(8.dp))

				Text(
					modifier = Modifier.align(Alignment.CenterHorizontally),
					text = "PACKAGE 1/1",
					fontFamily = FontFamily.ARCHIVO,
					fontSize = 12.sp,
					color = BrandColor.GRAY_500,
					fontWeight = FontWeight.Bold,
					letterSpacing = 1.5.sp
				)
			}
		}
	}
}

@Composable
private fun ColumnScope.BringToAthleteContent(
	orderDetails: OrderDetailsResponse? = null,
	onOrderPickupClicked: (String) -> Unit
) {
	Row {
		TextInfo(
			modifier = Modifier.weight(0.5f),
			label = stringResource(id = R.string.athlete_name),
			value = orderDetails?.athleteDetail?.athleteFullName()
		)

		TextInfo(
			modifier = Modifier.weight(0.5f),
			label = stringResource(id = R.string.proxy_name),
			value = orderDetails?.athleteDetail?.athleteProxyFullName()
		)
	}
	Spacer(modifier = Modifier.height(8.dp))
	Row {
		TextInfo(
			modifier = Modifier.weight(0.5f),
			label = stringResource(id = R.string.phone_number),
			value = orderDetails?.athleteDetail?.athletePhoneNumber
		)
		TextInfo(
			modifier = Modifier.weight(0.5f),
			label = stringResource(id = R.string.pickup_location),
			value = orderDetails?.athleteCheckInDetail?.athleteLocation
		)
	}
	Spacer(modifier = Modifier.height(16.dp))
	PrimaryButton(
		modifier = Modifier
			.fillMaxWidth(0.8f)
			.align(Alignment.CenterHorizontally),
		text = stringResource(id = R.string.confirm_pickup),
		onButtonClick = {
			orderDetails?.athleteCheckInDetail?.pickupTaskId?.let {
				onOrderPickupClicked(it.toString())
			}
		}
	)
}

@Composable
private fun StepNumberWithCircle(text: String?, circleColor: Color, textColor: Color) {
	Box(
		modifier = Modifier
			.size(32.dp)
			.clip(CircleShape)
			.background(color = circleColor),
		contentAlignment = Alignment.Center
	) {
		if (text == null) {
			Image(
				modifier = Modifier
					.padding(6.dp)
					.wrapContentSize(),
				painter = painterResource(id = R.drawable.ic_check_white),
				contentDescription = "Scan Image"
			)
		} else {
			Text(
				text = text,
				color = textColor,
// 				fontFamily = FontFamily.SANS,
				fontFamily = FontFamily.ARCHIVO,
				fontSize = 24.sp,
				fontWeight = FontWeight.Bold
			)
		}
	}
}

private fun cardTitleTextColor(isActive: Boolean) =
	if (isActive) BrandColor.BLUE_800_NT else BrandColor.BLUE_250_NT

private fun stepCountCircleColor(isActive: Boolean) =
	if (isActive) BrandColor.BLUE_800_NT else BrandColor.BLUE_250_NT

@PreviewPdt
@Composable
fun PickOrderScreenPreview() {
	PickOrderScreen(
		onBackButtonClick = {},
		orderDetails = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")),
		onOrderPickupClicked = {},
		onOrderPickupSuccess = {}
	)
}
