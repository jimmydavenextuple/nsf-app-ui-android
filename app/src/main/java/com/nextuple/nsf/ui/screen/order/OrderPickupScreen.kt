package com.nextuple.nsf.ui.screen.order

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.AthleteCheckInDetail
import com.nextuple.nsf.retrofit.dto.response.AthleteDetail
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.athleteFullName
import com.nextuple.nsf.retrofit.dto.response.athleteProxyFullName
import com.nextuple.nsf.ui.common.HoldSlip
import com.nextuple.nsf.ui.common.InfoCard
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.MultiOptionModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.common.chip.StatusChip
import com.nextuple.nsf.ui.component.ExpandableStepCard
import com.nextuple.nsf.ui.component.PrinterModal
import com.nextuple.nsf.ui.state.PrintViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.Printer
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.StringUtils.toPhoneNumberFormatted
import com.nextuple.nsf.util.TimeUtils
import com.nextuple.nsf.util.TimeUtils.formatTime

const val STEP_GET_ORDER: Int = 1
const val STEP_BRING_TO_ATHLETE: Int = 2

@Composable
fun OrderPickupScreen(
    printViewModel: PrintViewModel = hiltViewModel(),
    scanManager: ScanManager,
    completeOrderPickupState: GenericViewState = GenericViewState.Idle,
    orderNumber: String?,
    athleteDetail: AthleteDetail?,
    checkInDetail: AthleteCheckInDetail?,
    frDetail: FulfillmentRequestDetail?,
    holdSlipZpl: MutableList<String>?,
    defaultStep: Int = STEP_GET_ORDER,
    onOrderPickupClicked: (String) -> Unit,
    scanHoldSlipState: GenericViewState,
    onScanSuccess: (scanData: String) -> Unit,
    onOrderPickupSuccess: () -> Unit,
    onResetHoldSlipScan: () -> Unit,
    bypassPrinter: Boolean,
    printer: Printer,
    printerConnectionState: GenericViewState = GenericViewState.Idle,
    onPrintHoldSlip: (String) -> Unit,
    holdSlipState: GenericViewState = GenericViewState.Idle,
    resetGetHoldSlipState: () -> Unit,
    onConnectPrinter: (Printer, String) -> Unit,
    onResetPrinter: () -> Unit,
    ipPrefix: String?
) {
	val currentStep = remember {
		mutableStateOf(defaultStep)
	}
	val context = LocalContext.current

	var showConfirmPickupModal by remember { mutableStateOf(false) }
	var showPickupCompleteModal by remember { mutableStateOf(false) }
	var showPrinterModal by remember { mutableStateOf(false) }

	var scanState: MutableState<DetailedCallToActionMode>

	fun isOrderActive(): Boolean = currentStep.value == STEP_GET_ORDER
	fun isAthleteActive(): Boolean = currentStep.value == STEP_BRING_TO_ATHLETE
	fun advanceToAthleteStep() {
		currentStep.value = STEP_BRING_TO_ATHLETE
		onResetHoldSlipScan()
	}
	LaunchedEffect(Unit) {
		currentStep.value = defaultStep
	}

	LaunchedEffect(Unit) {
		scanManager.set { data, _ ->
			orderNumber?.let {
				if (data.isNotEmpty()) {
					onScanSuccess(data)
				}
			}
		}
	}

	val pickupTaskId = checkInDetail?.pickupTaskId
	val athleteName = athleteDetail?.athleteFullName().orEmpty()
	val proxyName = athleteDetail?.athleteProxyFullName().orEmpty()
	val phoneNumber = athleteDetail?.athletePhoneNumber.orEmpty()
	val holdingLocation = frDetail?.containers?.firstOrNull()?.holdingLocation.orEmpty()
	val pickupLocation = checkInDetail?.athleteLocation.orEmpty()
	val carInfo = checkInDetail?.athleteVehicle.orEmpty()
	val checkInTime = checkInDetail?.checkInTime.orEmpty()

	Box(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding()
				.background(BrandColor.GRAY_100)
				.verticalScroll(rememberScrollState())
		) {
			when (scanHoldSlipState) {
				GenericViewState.Loading -> {
					scanState = remember {
						mutableStateOf(DetailedCallToActionMode.Loading())
					}
				}
				GenericViewState.Success -> {
					scanState = remember {
						mutableStateOf(
							DetailedCallToActionMode.Done(
								BrandColor.WHITE,
								BrandColor.BLUE_800_NT
							)
						)
					}
					Handler(Looper.getMainLooper()).postDelayed({
						advanceToAthleteStep()
					}, 1000)
				}
				else -> {
					scanState = remember {
						mutableStateOf(DetailedCallToActionMode.Scan(""))
					}
				}
			}

			OrderPickupCardItem(
				stepNumber = "1",
				title = stringResource(id = R.string.get_order_title),
				isActive = isOrderActive(),
				extraContent = {
					GetOrdersContent(
						athleteName = athleteName,
						holdingLocation = holdingLocation,
						onScanClicked = {
							if (orderNumber != null) {
								onScanSuccess(orderNumber)
							}
						},
						holdSlipState = scanState
					)
					ClickableText(
						text = AnnotatedString("Reprint A Hold Slip".uppercase()),
						style = TextStyle(
							fontSize = 12.sp,
							fontFamily = FontFamily.ARCHIVO,
							fontWeight = FontWeight(700),
							color = BrandColor.BLACK,
							textAlign = TextAlign.Center,
							letterSpacing = 1.5.sp,
							textDecoration = TextDecoration.Underline
						),
						onClick = {
							if (printer.connectionStatus) {
								frDetail?.fulfillmentRequestNumber?.let {
									onPrintHoldSlip(it)
								}
							} else {
								showPrinterModal = true
							}
						}
					)
				}
			)

			OrderPickupCardItem(
				stepNumber = "2",
				title = stringResource(id = R.string.bring_to_athlete_title),
				isActive = isAthleteActive(),
				extraContent = {
					BringToAthleteContent(
						athleteName = athleteName,
						proxyName = proxyName,
						phoneNumber = phoneNumber,
						pickupLocation = pickupLocation,
						carInfo = carInfo,
						onOrderPickupClicked = {
							showConfirmPickupModal = true
						}
					)
					Spacer(modifier = Modifier.size(20.dp))
				}
			)

			if (showConfirmPickupModal) {
				ConfirmPickupModal(
					confirmPickupAction = {
						showConfirmPickupModal = false
						pickupTaskId?.let {
							onOrderPickupClicked(it.toString())
						}
					},
					closeModal = {
						showConfirmPickupModal = false
					}
				)
			}

			if (showPickupCompleteModal) {
				val completePickup: () -> Unit = {
					showPickupCompleteModal = false
					onOrderPickupSuccess()
				}
				val deliverySpeed = TimeUtils.calculateTimeDifferenceInSeconds(checkInTime)

				if (deliverySpeed != null) {
					PickupCompleteModal(
						deliverySpeed = deliverySpeed.formatTime(),
						onOrderPickupSuccess = completePickup
					)
				} else {
					completePickup()
				}
			}

			if (showPrinterModal) {
				PrinterModal(
					ipPrefix = ipPrefix,
					printer = printer,
					onReset = onResetPrinter,
					printerConnectionState = printerConnectionState,
					toggleModal = { showPrinterModal = it },
					onConnectPrinter = onConnectPrinter
				)
			}
			// Todo: and refactor. Same as OrderDetailsScreen
			if (holdSlipState == GenericViewState.Success) {
				resetGetHoldSlipState()
				printViewModel.printHoldSlip(holdSlipZpl!!, printer, bypassPrinter)
			} else if (holdSlipState == GenericViewState.Failure) {
				Toast.makeText(
					context,
					stringResource(R.string.hold_slip_retrieve_error),
					Toast.LENGTH_SHORT
				).show()
			}
			if (printViewModel.holdSlipPrintState == GenericViewState.Success) {
				Toast.makeText(
					context,
					stringResource(R.string.hold_slip_print_success),
					Toast.LENGTH_SHORT
				).show()
				printViewModel.resetHoldSlipPrintState()
			} else if (printViewModel.holdSlipPrintState == GenericViewState.Failure) {
				Toast.makeText(
					context,
					stringResource(R.string.hold_slip_print_error),
					Toast.LENGTH_SHORT
				).show()
				printViewModel.resetHoldSlipPrintState()
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
			showPickupCompleteModal = true
		}
	}
}

@Composable
private fun OrderPickupCardItem(
	stepNumber: String?,
	title: String,
	isActive: Boolean,
	extraContent: @Composable () -> Unit = {}
) {
	ExpandableStepCard(stepNumber = stepNumber, title = title, isActive = isActive, isComplete = !isActive && stepNumber == "1") {
		extraContent()
	}
}

@Composable
private fun GetOrdersContent(
	athleteName: String,
	holdingLocation: String,
	holdSlipState: MutableState<DetailedCallToActionMode>,
	onScanClicked: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 8.dp, bottom = 8.dp),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_100)
	) {
		Row(
			modifier = Modifier
				.fillMaxSize()
				.padding(12.dp)
		) {
			Column(
				modifier = Modifier
					.fillMaxHeight()
					.fillMaxWidth()
					.weight(1f),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Image(painter = painterResource(id = R.drawable.ic_package_location), contentDescription = "")
					Spacer(modifier = Modifier.width(5.dp))
					Text(
						text = holdingLocation,
						style = TextStyle(
							fontSize = 14.sp,
							lineHeight = 18.2.sp,
							fontFamily = FontFamily.ARCHIVO,
							fontWeight = FontWeight(700),
							letterSpacing = 0.5.sp
						)
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.clickable {
							if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
								onScanClicked()
							}
						},
					horizontalArrangement = Arrangement.Center
				) {
					val lastName = athleteName.split(" ")
					HoldSlip(
						athleteName = athleteName,
						lastName = lastName[lastName.lastIndex],
						packageNum = "1",
						totalPackageNum = "1",
						isSmallSize = true,
						isScanable = true,
						isSelected = false,
						scanStatus = holdSlipState.value
					)
				}
			}
		}
	}
}

@Composable
fun ColumnScope.BringToAthleteContent(
	athleteName: String,
	proxyName: String,
	phoneNumber: String,
	pickupLocation: String,
	carInfo: String,
	onOrderPickupClicked: () -> Unit
) {
	val labelsToValues = linkedMapOf(
		stringResource(id = R.string.name) to athleteName,
		stringResource(id = R.string.phone_number) to toPhoneNumberFormatted(phoneNumber)
	)

	if (proxyName.isNotEmpty()) {
		labelsToValues[stringResource(id = R.string.proxy_name)] = proxyName
	}

	if (pickupLocation.isNotEmpty()) {
		labelsToValues[stringResource(id = R.string.pickup_location)] = pickupLocation
	}

	if (carInfo.isNotEmpty()) {
		labelsToValues[stringResource(id = R.string.car_info)] = carInfo
	}

	InfoCard(
		shape = RectangleShape,
		internalPadding = 0.dp,
		internalSpacedBy = 12.dp,
		labelsToValues = labelsToValues
	)
	Spacer(modifier = Modifier.height(24.dp))
	PrimaryButton(
		modifier = Modifier
			.fillMaxWidth(0.8f)
			.align(Alignment.CenterHorizontally),
		text = stringResource(id = R.string.complete_pickup),
		onButtonClick = onOrderPickupClicked
	)
}

@Preview
@Composable
private fun ConfirmPickupModal(
	confirmPickupAction: () -> Unit = {},
	closeModal: () -> Unit = {}
) {
	MultiOptionModal(
		title = stringResource(id = R.string.confirm_pickup_modal_title),
		subTitle = stringResource(id = R.string.confirm_pickup_modal_info),
		buttons = listOf("CONFIRM", "BACK"),
		buttonClick = { buttonClicked ->
			if (buttonClicked == "CONFIRM") confirmPickupAction() else closeModal()
		},
		crossIconClick = closeModal,
		dismissOnBackPress = false,
		dismissOnClickOutside = false,
		onDismissRequest = { }
	)
}

@Composable
private fun PickupCompleteModal(
	modifier: Modifier = Modifier,
	deliverySpeed: String,
	onOrderPickupSuccess: () -> Unit = {}
) {
	InfoModal(
		modifier = modifier,
		title = stringResource(id = R.string.pickup_complete),
		visualContent = {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				StatusChip(
					modifier = modifier
						.border(
							width = 2.dp,
							color = BrandColor.BLUE_300_NT,
							shape = RoundedCornerShape(size = 3.dp)
						)
						.background(
							color = BrandColor.TRANSPARENT,
							shape = RoundedCornerShape(size = 3.dp)
						),
					statusText = deliverySpeed,
					statusColor = BrandColor.BLUE_300_NT,
					fontSize = 16.sp
				)
				Text(
					modifier = Modifier.padding(top = 8.dp),
					text = stringResource(id = R.string.delivery_speed).uppercase(),
					style = TextStyle(
						fontSize = 12.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(700),
						color = BrandColor.BLACK,
						letterSpacing = 1.5.sp
					)
				)
			}
		},
		buttonText = stringResource(id = R.string.ok),
		buttonClick = {
			onOrderPickupSuccess()
		},
		crossIconClick = {
			onOrderPickupSuccess()
		},
		dismissOnBackPress = false,
		dismissOnClickOutside = false,
		onDismissRequest = { }
	)
}

@Preview
@Composable
private fun PickupCompleteModalPreview() {
	PickupCompleteModal(
		deliverySpeed = "02:26.52"
	)
}

@PreviewPdt
@Composable
fun PickOrderScreenPreview() {
	OrderPickupScreen(
		scanManager = NoOpScanManager(),
		orderNumber = "123456",
		athleteDetail = AthleteDetail(
			athleteFirstName = "Anna",
			athleteLastName = "Heisey",
			athleteProxyFirstName = "Eli",
			athleteProxyLastName = "Heisey",
			athletePhoneNumber = "5087695491"
		),
		checkInDetail = AthleteCheckInDetail(
			athleteLocation = "Curbside Spot #2"
		),
		frDetail = FulfillmentRequestDetail(
			fulfillmentRequestNumber = "123456.001",
			holdingLocation = "Main Holding Area, Bin 10"
		),
		holdSlipZpl = mutableListOf(),
		onOrderPickupClicked = {},
		onOrderPickupSuccess = {},
		scanHoldSlipState = GenericViewState.Success,
		onScanSuccess = {},
		onResetHoldSlipScan = {},
		resetGetHoldSlipState = {},
		ipPrefix = "",
		printer = Printer(printerName = "BOPIS", ipAddress = "", connectionStatus = false),
		onPrintHoldSlip = {},
		onConnectPrinter = { _, _ -> },
		onResetPrinter = {},
		bypassPrinter = false
	)
}

@Preview
@Composable
private fun BringToAthletePreview() {
	Column {
		OrderPickupCardItem(
			stepNumber = "2",
			title = stringResource(id = R.string.bring_to_athlete_title),
			isActive = true,
			extraContent = {
				BringToAthleteContent(
					athleteName = "Thomas Jefferson",
					proxyName = "George Clinton",
					phoneNumber = "(412) 123 - 4567",
					pickupLocation = "Curbside Spot 2",
					carInfo = "White Ford Sedan",
					onOrderPickupClicked = {}
				)
				Spacer(modifier = Modifier.size(20.dp))
			}
		)
	}
}
