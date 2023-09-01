package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.retrofit.dto.response.athleteShortName
import com.nextuple.nsf.ui.common.StatusText
import com.nextuple.nsf.ui.common.TopLabeledTextField
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.OrderScreenTabsEnum
import com.nextuple.nsf.util.OrderStatusEnum
import com.nextuple.nsf.util.TimeUtils
import kotlinx.coroutines.delay

@Composable
fun OrderScreen(
	scanManager: ScanManager,
	viewState: GenericViewState = GenericViewState.Loading,
	orderDetailsState: GenericViewState = GenericViewState.Idle,
	homeSearchInput: String,
	getOrders: (String?, String?) -> Unit,
	getOrderDetails: (String) -> Unit,
	getOrderDetailsCompletion: () -> Unit,
	allOrderList: List<OrderDetailsResponse>?,
	pickupOrderList: List<OrderDetailsResponse>?
) {
	val searchInputText = rememberSaveable { mutableStateOf(homeSearchInput) }
	val initialClearSearchState = rememberSaveable { mutableStateOf(false) }
	var selectedTab by rememberSaveable { mutableStateOf(OrderScreenTabsEnum.ALL) }

	LaunchedEffect(Unit) {
		// If condition added for (SEARCH > PICKUP > ORDER DETAILS > BACK TO PICKUP)
		getOrders(
			if (selectedTab == OrderScreenTabsEnum.ALL && searchInputText.value.isNotBlank()) searchInputText.value else null,
			if (selectedTab == OrderScreenTabsEnum.ALL) null else "0"
		)
	}

	Box(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp)
			) {
				CustomTab(tab = OrderScreenTabsEnum.ALL, selectedTab = selectedTab) {
					if (selectedTab == OrderScreenTabsEnum.ALL) {
						searchInputText.value = ""
						initialClearSearchState.value = false
					}
					selectedTab = OrderScreenTabsEnum.ALL
					getOrders(searchInputText.value, null)
				}
				CustomTab(tab = OrderScreenTabsEnum.PICKUP, selectedTab = selectedTab) {
					selectedTab = OrderScreenTabsEnum.PICKUP
					getOrders(null, "0")
				}
			}
			if (selectedTab == OrderScreenTabsEnum.ALL) {
				AllTabContainer(
					scanManager = scanManager,
					getOrders = getOrders,
					existingText = searchInputText,
					existingClearSearchState = initialClearSearchState
				)
				if (viewState == GenericViewState.Success && searchInputText.value.isNotBlank()) {
					Text(
						modifier = Modifier
							.fillMaxWidth()
							.padding(start = 17.dp, end = 17.dp, bottom = 8.dp),
						text = "Showing results for \"${searchInputText.value}\"",
						textAlign = TextAlign.Start,
						color = BrandColor.GRAY_900,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight.Normal,
						fontStyle = FontStyle.Normal,
						fontSize = 12.sp
					)
				}
				allOrderList?.forEach {
					OrderCard(
						modifier = Modifier
							.fillMaxWidth()
							.padding(start = 17.dp, end = 17.dp, bottom = 10.dp),
						order = it,
						onClick = {
							it.fulfillmentRequestDetail.fulfillmentRequestNumber.let { fulfillmentRequestNumber ->
								getOrderDetails(
									fulfillmentRequestNumber
								)
							}
						}
					)
				}
			} else if (selectedTab == OrderScreenTabsEnum.PICKUP) {
				PickupTabContainer(pickupOrderList, getOrderDetails = getOrderDetails)
			}
		}
		if (viewState == GenericViewState.Loading || orderDetailsState == GenericViewState.Loading) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center),
				color = BrandColor.GRAY_900
			)
		} else if (orderDetailsState == GenericViewState.Success) {
			getOrderDetailsCompletion.invoke()
		}
	}
}

@Composable
private fun RowScope.CustomTab(
	tab: OrderScreenTabsEnum,
	selectedTab: OrderScreenTabsEnum,
	onClick: () -> Unit
) {
	Box(
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.5f, fill = true)
			.clickable { onClick() }
			.background(
				if (selectedTab == tab) {
					BrandColor.GRAY_50
				} else {
					BrandColor.GRAY_700
				}
			)
	) {
		Text(
			modifier = Modifier
				.align(Alignment.Center),
			text = tab.name,
			fontSize = 14.sp,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight(700),
			color = BrandColor.GRAY,
			textAlign = TextAlign.Center,
			letterSpacing = 1.5.sp
		)
	}
}

@Composable
private fun AllTabContainer(
	scanManager: ScanManager,
	getOrders: (String?, String?) -> Unit,
	existingText: MutableState<String>,
	existingClearSearchState: MutableState<Boolean>
) {
	val focusManager = LocalFocusManager.current
	val isInvalid = remember { mutableStateOf(false) }
	val isClearState by remember { mutableStateOf(existingClearSearchState) }
	val errorMessage = remember { mutableStateOf("") }
	val searchInput by rememberSaveable { mutableStateOf(existingText) }

	// Colors will change based on input search
	val searchBackgroundColor: Color
	val searchTextColor: Color
	val searchTintColor: Color

	if (searchInput.value.isNotBlank()) {
		searchBackgroundColor = BrandColor.ORANGE_600
		searchTextColor = BrandColor.GRAY_50
		searchTintColor = BrandColor.GRAY_50
	} else {
		searchBackgroundColor = BrandColor.GRAY_200
		searchTextColor = BrandColor.GRAY_600
		searchTintColor = BrandColor.GRAY_600
	}

	fun resetSearchState() {
		searchInput.value = ""
		isClearState.value = false
		isInvalid.value = false
		focusManager.clearFocus()
		getOrders(null, null)
	}

	fun performSearchSearch() {
		isClearState.value = true
		focusManager.clearFocus()
		getOrders(searchInput.value, null)
	}

	LaunchedEffect(Unit) {
		scanManager.set { data, _ ->
			searchInput.value = data
		}
	}
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 39.dp, top = 12.dp, end = 36.dp)
	) {
		TopLabeledTextField(
			modifier = Modifier.fillMaxWidth(0.75f),
			labelText = stringResource(id = R.string.order_number_or_last_name),
			fieldValue = searchInput.value,
			isInvalid = isInvalid.value,
			onValueChange = {
				searchInput.value = it
				isInvalid.value = false
				isClearState.value = false
			},
			errorMessage = errorMessage.value,
			textFieldShape = RoundedCornerShape(
				topStart = 6.dp,
				bottomStart = 6.dp,
				topEnd = 0.dp,
				bottomEnd = 0.dp
			),
			keyboardActions = KeyboardActions(onDone = {
				performSearchSearch()
			})
		)
		Surface(
			shape = RoundedCornerShape(
				topStart = 0.dp,
				bottomStart = 0.dp,
				topEnd = 6.dp,
				bottomEnd = 6.dp
			),
			modifier = Modifier
				.fillMaxWidth(0.25f)
				.padding(top = 1.dp, start = 0.dp)
				.align(Alignment.CenterEnd)
		) {
			Column(
				modifier = Modifier
					.height(40.dp)
					.align(Alignment.CenterEnd)
					.background(color = searchBackgroundColor)
					.clickable(enabled = searchInput.value.isNotBlank()) {
						if (isClearState.value) {
							resetSearchState()
						} else {
							performSearchSearch()
						}
					},

				verticalArrangement = Arrangement.Center
			) {
				Icon(
					modifier = Modifier
						.align(Alignment.CenterHorizontally)
						.size(24.dp),
					imageVector = if (isClearState.value) {
						ImageVector.vectorResource(R.drawable.ic_close)
					} else {
						ImageVector.vectorResource(
							R.drawable.ic_order_search
						)
					},
					tint = searchTintColor,
					contentDescription = ""
				)
				Text(
					text = if (isClearState.value) {
						stringResource(id = R.string.clear).uppercase()
					} else {
						stringResource(
							id = R.string.search
						).uppercase()
					},
					style = TextStyle(
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight.Bold,
						fontStyle = FontStyle.Normal,
						letterSpacing = 1.5.sp,
						fontSize = 10.sp
					),
					modifier = Modifier.align(Alignment.CenterHorizontally),
					color = searchTextColor
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderCard(
	modifier: Modifier = Modifier,
	order: OrderDetailsResponse,
	onClick: () -> Unit
) {
	var elapsedTime by remember {
		mutableStateOf(
			if (order.athleteCheckInDetail?.checkInTime == null) {
				0
			} else {
				TimeUtils.calculateTimeDifferenceInSeconds(
					order.athleteCheckInDetail.checkInTime
				)
			}
		)
	}
	var isRunning by remember { mutableStateOf(false) }

	val lifecycleOwner = LocalLifecycleOwner.current

	LaunchedEffect(isRunning, lifecycleOwner) {
		if (isRunning) {
			while (true) {
				delay(1000) // Delay for 1 second
				elapsedTime = elapsedTime?.plus(1)
			}
		}
	}

	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		onClick = onClick,
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_100),
		elevation = CardDefaults.cardElevation(3.dp)

	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
		) {
			if (order.orderStatusText?.uppercase()?.startsWith(OrderStatusEnum.CURBSIDE.name) == true) {
				Box(
					modifier = Modifier
						.padding(start = 8.dp)
						.align(Alignment.CenterVertically)
						.width(8.dp)
						.height(84.dp)
						.background(
							color = BrandColor.RED,
							shape = RoundedCornerShape(size = 4.dp)
						)
				)
			}

			Column(
				modifier = Modifier
					.weight(0.5f)
					.padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
			) {
				order.athleteDetail?.athleteShortName()?.let {
					Text(
						text = it,
						fontSize = 20.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(700),
						color = BrandColor.GRAY_900,
						letterSpacing = 0.5.sp
					)
				}

				Spacer(modifier = Modifier.height(12.dp))

				order.fulfillmentRequestDetail.let {
					Text(
						text = stringResource(id = R.string.location),
						fontSize = 12.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(700),
						color = BrandColor.BLACK,
						letterSpacing = 1.5.sp
					)

					OrderDetailsText(text = "${it.holdingLocation}")
				}
			}

			Column(
				modifier = Modifier
					.weight(0.5f)
					.padding(end = 12.dp, top = 12.dp, bottom = 12.dp),
				horizontalAlignment = Alignment.End
			) {
				order.orderStatusText?.let {
					StatusText(
						status = it,
						backgroundColor = getStatusTextColor(it)
					)
				}
				Spacer(modifier = Modifier.height(11.dp))
				if (order.orderStatusText?.uppercase() != OrderStatusEnum.COMPLETED.name) {
					order.athleteCheckInDetail?.checkInTime?.let {
						isRunning = true
						StatusText(status = TimeUtils.formatTime(elapsedTime), backgroundColor = BrandColor.RED)
					}
				}
				if (order.athleteCheckInDetail?.checkInTime == null) {
					Spacer(modifier = Modifier.height(33.dp))
				} else {
					Spacer(modifier = Modifier.height(11.dp))
				}
				order.fulfillmentRequestDetail.fulfillmentType?.let {
					Text(
						modifier = Modifier.padding(end = 8.dp),
						text = it,
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
}

@Composable
private fun OrderDetailsText(text: String) {
	Text(
		text = text,
		fontSize = 14.sp,
		fontFamily = FontFamily.ARCHIVO,
		color = BrandColor.BLACK,
		letterSpacing = 0.5.sp
	)
}

@Composable
private fun PickupTabContainer(pickupOrderList: List<OrderDetailsResponse>?, getOrderDetails: (String) -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
	) {
		Spacer(modifier = Modifier.height(10.dp))
		pickupOrderList?.forEach {
			OrderCard(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 17.dp, end = 17.dp, bottom = 10.dp),
				order = it,
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

fun getStatusTextColor(text: String): Color {
	/*
		TODO: Revisit Logic once fit-goat-api is fully ready
	 */
	return when {
		text.uppercase().startsWith(OrderStatusEnum.READY.name) -> {
			BrandColor.BLUE_900
		}

		text.uppercase().startsWith(OrderStatusEnum.EXTENDED.name) -> {
			BrandColor.BLUE_900
		}

		text.uppercase().contains(OrderStatusEnum.PROGRESS.name) -> {
			BrandColor.BLUE_900
		}

		text.uppercase().startsWith(OrderStatusEnum.COMPLETED.name) -> {
			BrandColor.GREEN_500
		}

		text.uppercase().startsWith(OrderStatusEnum.CURBSIDE.name) -> {
			BrandColor.RED
		}

		text.uppercase().startsWith(OrderStatusEnum.AGED.name) -> {
			BrandColor.GRAY_1000
		}

		else -> {
			BrandColor.GRAY_1000
		}
	}
}

@Composable
@PreviewPdt
fun PreviewOrderScreen() {
	OrderScreen(
		scanManager = NoOpScanManager(),
		homeSearchInput = "",
		getOrders = { _, _ -> },
		getOrderDetails = {},
		getOrderDetailsCompletion = {},
		allOrderList = null,
		pickupOrderList = null
	)
}
