package com.nextuple.nsf.ui.screen.order

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.AthleteDetail
import com.nextuple.nsf.retrofit.dto.response.DeclineCode
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.ui.common.ActionsCard
import com.nextuple.nsf.ui.common.ActionsCardAction
import com.nextuple.nsf.ui.common.InfoCard
import com.nextuple.nsf.ui.common.MultiOptionModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.component.EditLocation
import com.nextuple.nsf.ui.component.PrinterModal
import com.nextuple.nsf.ui.state.CancelReasonData
import com.nextuple.nsf.ui.state.Printer
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.FRStatus
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.OrderStatus
import com.nextuple.nsf.util.SubFulfillmentType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val YES = "YES"
const val NO = "NO"
const val CONFIRM = "CONFIRM"
const val BACK = "BACK"

@Composable
fun OrderDetailsScreen(
	viewState: GenericViewState = GenericViewState.Idle,
	startPickupViewState: GenericViewState = GenericViewState.Idle,
	athleteName: String,
	athleteProxyName: String,
	athletePhoneNumber: String,
	orderNumber: String,
	orderDate: String,
	expectedDate: String,
	receivedDate: String,
	packedOnDate: String,
	holdingLocation: String,
	holdingAreas: List<String>,
	orderDetailsResponse: OrderDetailsResponse? = null,
	declineModalOptions: List<DeclineCode>?,
	onStartPickup: (String) -> Unit,
	onPickupExtend: (String) -> Unit,
	onPickupRemoveCheckIn: (String) -> Unit,
	onStartPickupCompletion: () -> Unit,
	ipPrefix: String?,
	printer: Printer,
	printerConnectionState: GenericViewState = GenericViewState.Idle,
	holdSlipState: GenericViewState = GenericViewState.Idle,
	printHoldSlipState: GenericViewState = GenericViewState.Idle,
	onPrintHoldSlip: (String) -> Unit,
	onPrintHoldSlipSuccessCallBack: () -> Unit,
	onResetPrintHoldSlip: () -> Unit,
	onConnectPrinter: (Printer, String) -> Unit,
	onResetPrinter: () -> Unit,
	onCancelOrder: (String, DeclineCode, Boolean) -> Unit,
	onCancelAgedError: (error: String) -> Unit,
	cancelReasonData: CancelReasonData,
	onCancelComplete: () -> Unit,
	scanLocationState: GenericViewState = GenericViewState.Idle,
	resetScanLocationState: () -> Unit,
	onLocationChange: (containerId: Long, holdingLocation: String) -> Unit,
	onPackOrder: (frNo: String) -> Unit,
	scanManager: ScanManager?
) {
	val context = LocalContext.current

	fun enableStartPickupButton(orderDetails: OrderDetailsResponse?) =
		!orderDetails?.fulfillmentRequestDetail?.fulfillmentRequestStatus?.code.equals(
			FRStatus.PICKUP_COMPLETED.code
		) // i.e. NOT PICKUP_COMPLETED

	fun enableRemoveCheckInButton(orderDetails: OrderDetailsResponse?) =
		orderDetails?.fulfillmentRequestDetail?.fulfillmentRequestStatus?.code.equals(
			FRStatus.ATHLETE_CHECKED_IN.code
		)

	fun enableExtendPickupButton(orderDetails: OrderDetailsResponse?): Boolean {
		return orderDetails?.athleteCheckInDetail?.pickupTaskId == null &&
			(orderDetails?.pickupExtendedCount == null || orderDetails.pickupExtendedCount == 0)
	}

	var showExtendInfoModal by remember { mutableStateOf(false) }

	var showAgedCancel by remember { mutableStateOf(false) }

	var currentHoldingLocation by remember { mutableStateOf(holdingLocation) }

	fun dialogExtendPickupVisibility(visibility: Boolean) {
		showExtendInfoModal = visibility
	}

	var showRemoveCheckInInfoModal by remember { mutableStateOf(false) }

	fun dialogRemoveCheckInVisibility(visibility: Boolean) {
		showRemoveCheckInInfoModal = visibility
	}

	fun onExtendInfoModelClick(buttonText: String) {
		if (buttonText == CONFIRM) {
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

	fun onCancelReasonClicked(buttonText: String) {
		val frNumber =
			orderDetailsResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber.orEmpty()
		val cancelReason = declineModalOptions?.find { it.displayName == buttonText }

		if (cancelReason != null) {
			onCancelOrder(frNumber, cancelReason, false)
			onCancelComplete()
		}
	}

	var showCancelConfirmationModal by remember { mutableStateOf(false) }
	var showCancelReasonModal by remember { mutableStateOf(false) }
	var showPrinterModal by remember { mutableStateOf(false) }
	var showEditLocationModal by remember { mutableStateOf(false) }

	val orderStatusText = orderDetailsResponse?.orderStatusText.orEmpty()
	val orderStatus = OrderStatus.getByStatus(orderStatusText)
	val isAged = orderStatus == OrderStatus.AGED
	val orderType = orderDetailsResponse?.fulfillmentRequestDetail?.subFulfillmentType.orEmpty()
	val checkInTime = orderDetailsResponse?.athleteCheckInDetail?.checkInTime

	val pickedBy = orderDetailsResponse?.pickedByUserId?.ifEmpty { null }
	val packedBy = orderDetailsResponse?.packedByUserId?.ifEmpty { null }
	val stagedBy = orderDetailsResponse?.stagedByUserId?.ifEmpty { null }
	val teammateInfo = when (orderStatus) {
		OrderStatus.PACK -> pickedBy?.let { stringResource(R.string.picked_by) to it }
		OrderStatus.STAGE -> packedBy?.let { stringResource(R.string.packed_by) to it }
		OrderStatus.BEING_PACKED -> packedBy?.let { stringResource(R.string.packer) to it }
		OrderStatus.BEING_STAGED -> stagedBy?.let { stringResource(R.string.stager) to it }
		else -> stagedBy?.let { stringResource(R.string.staged_by) to it }
	}

	val athleteInfoStatus = listOf(
		OrderStatus.CHECKED_IN,
		OrderStatus.COMPLETED,
		OrderStatus.EXTENDED,
		OrderStatus.CANCELED,
		OrderStatus.AGED
	)

	val orderInfoStatus = listOf(
		OrderStatus.READY,
		OrderStatus.DECLINED,
		OrderStatus.INCOMING,
		OrderStatus.LATE,
		OrderStatus.PACK,
		OrderStatus.BEING_PACKED,
		OrderStatus.STAGE,
		OrderStatus.BEING_STAGED
	)

	Column(
		modifier = Modifier
			.background(BrandColor.GRAY_100)
			.fillMaxSize()
			.padding(start = 24.dp, end = 24.dp, top = 12.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		val athleteInfoTitles = if (orderDetailsResponse?.athleteCheckInDetail?.checkInType?.contains("curbside", true) == true) {
			linkedMapOf(
				stringResource(id = R.string.name) to athleteName,
				stringResource(id = R.string.proxy_name) to athleteProxyName,
				stringResource(id = R.string.phone_number) to athletePhoneNumber,
				stringResource(id = R.string.car_info) to (orderDetailsResponse.athleteCheckInDetail.athleteVehicle ?: "")
			)
		} else {
			linkedMapOf(
				stringResource(id = R.string.name) to athleteName,
				stringResource(id = R.string.proxy_name) to athleteProxyName,
				stringResource(id = R.string.phone_number) to athletePhoneNumber
			)
		}
		InfoCard(
			title = stringResource(id = R.string.athlete_info),
			status = if (athleteInfoStatus.contains(orderStatus)) orderStatus.statusText else null,
			athleteCheckInTime = checkInTime?.takeIf { orderStatus != OrderStatus.COMPLETED },
			labelsToValues = athleteInfoTitles
		) {
			if (isAged) {
				if (showAgedCancel) {
					if (!orderDetailsResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber.isNullOrEmpty()) {
						val frNumber =
							orderDetailsResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber.orEmpty()
						OrderCancelModal(
							isDamaged = cancelReasonData.isDamaged,
							onCancelComplete = {
								onCancelOrder(
									frNumber,
									DeclineCode(id = "ABANDON", displayName = "ABANDON"),
									true
								)
								onCancelComplete()
								showAgedCancel = false
							},
							onDismissRequest = { showAgedCancel = false }
						)
					} else {
						Toast.makeText(
							context,
							stringResource(id = R.string.cancel_error),
							Toast.LENGTH_SHORT
						).show()
						onCancelAgedError("There was an error trying to pull in FR information to cancel an aged order")
					}
				}
				AgedCancel(
					modifier = Modifier
						.padding(vertical = 8.dp)
						.testTag("AgedCancelCard"),
					onAgedCancel = {
						showAgedCancel = true
					}
				)
			}
		}
		if (!showEditLocationModal) {
			val orderInfo = mutableListOf(
				stringResource(id = R.string.order_number) to orderNumber,
				stringResource(id = R.string.order_type) to orderType,
				stringResource(id = R.string.order_date) to orderDate
			).apply {
				if (orderType == SubFulfillmentType.BOPL.name) {
					if (expectedDate.isNotEmpty()) {
						add(stringResource(id = R.string.expected) to expectedDate)
					}
					if (receivedDate.isNotEmpty()) {
						add(stringResource(id = R.string.received_on) to receivedDate)
					}
				}
				if (packedOnDate.isNotEmpty()) {
					if (orderStatus == OrderStatus.READY || orderStatus == OrderStatus.EXTENDED) {
						add(stringResource(id = R.string.prepped_on) to packedOnDate)
					} else {
						add(stringResource(id = R.string.packed_on) to packedOnDate)
					}
				}
				if (currentHoldingLocation.isNotEmpty()) {
					add(stringResource(id = R.string.location) to currentHoldingLocation)
				}
				if (teammateInfo != null) {
					add(teammateInfo)
				}
			}.toTypedArray()

			InfoCard(
				title = stringResource(id = R.string.order_info),
				status = if (orderInfoStatus.contains(orderStatus)) orderStatus.statusText else null,
				labelsToValues = linkedMapOf(*orderInfo)
			)
		}
		val packedItemList = orderDetailsResponse?.fulfillmentRequestDetail?.containers?.flatMap {
			it.packedItems
		}
		OrderContentsDropdown(
			startExpanded = OrderStatus.isCompleteStatus(orderStatus) || orderStatus == OrderStatus.BEING_PACKED || orderStatus == OrderStatus.BEING_STAGED,
			isBopl = orderType == SubFulfillmentType.BOPL.name,
			packedItemList = packedItemList
		)

		if (OrderStatus.isReadyStatus(orderStatus)) {
			ActionsCard(
				title = stringResource(id = R.string.pickup_actions),
				primaryAction = ActionsCardAction(
					label = stringResource(id = R.string.start_pickup),
					isEnabled = enableStartPickupButton(orderDetailsResponse),
					onClick = {
						orderDetailsResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber?.let {
							onStartPickup(it)
						}
					}
				),
				secondaryActions = listOf(
					ActionsCardAction(
						label = stringResource(id = R.string.extend_pickup),
						isEnabled = enableExtendPickupButton(orderDetailsResponse),
						onClick = {
							dialogExtendPickupVisibility(true)
						}
					),
					ActionsCardAction(
						label = stringResource(id = R.string.remove_check_in),
						isEnabled = enableRemoveCheckInButton(orderDetailsResponse),
						onClick = {
							dialogRemoveCheckInVisibility(true)
						}
					)
				)
			)
		}

		OrderManagementCard(
			orderStatus = orderStatus,
			printer = printer,
			showCancelButton = !isAged,
			onPrintHoldSlipEvent = {
				orderDetailsResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber?.let {
					onPrintHoldSlip(it)
				}
			},
			showPrinterModalEvent = { showPrinterModal = true },
			showCancelConfirmationModalEvent = { showCancelConfirmationModal = true },
			showEditLocationModalEvent = {
				resetScanLocationState()
				showEditLocationModal = true
			},
			onPackOrder = {
				orderDetailsResponse?.fulfillmentRequestDetail?.fulfillmentRequestNumber?.let {
					onPackOrder(it)
				}
			}
		)
	}
	if (viewState == GenericViewState.Loading ||
		startPickupViewState == GenericViewState.Loading ||
		cancelReasonData.state == GenericViewState.Loading
	) {
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

	if (holdSlipState == GenericViewState.Success) {
		onPrintHoldSlipSuccessCallBack.invoke()
	} else if (holdSlipState == GenericViewState.Failure) {
		Toast.makeText(
			context,
			stringResource(R.string.hold_slip_retrieve_error),
			Toast.LENGTH_SHORT
		).show()
	}

	if (printHoldSlipState == GenericViewState.Success) {
		Toast.makeText(
			context,
			stringResource(R.string.hold_slip_print_success),
			Toast.LENGTH_SHORT
		).show()
		onResetPrintHoldSlip()
	} else if (printHoldSlipState == GenericViewState.Failure) {
		Toast.makeText(context, stringResource(R.string.hold_slip_print_error), Toast.LENGTH_SHORT)
			.show()
		onResetPrintHoldSlip()
	}

	if (cancelReasonData.state == GenericViewState.Failure) {
		Toast.makeText(context, stringResource(id = R.string.cancel_error), Toast.LENGTH_SHORT)
			.show()
	}

	if (showCancelConfirmationModal) {
		CancelConfirmationModal(
			onConfirm = { showCancelReasonModal = true },
			onCancel = { },
			dismissModal = { showCancelConfirmationModal = false }
		)
	}

	if (showCancelReasonModal) {
		CancelReasonModal(
			cancelReasons = declineModalOptions?.map { it.displayName } ?: emptyList(),
			onCancelReasonClicked = {
				onCancelReasonClicked(it)
			},
			dismissModal = { showCancelReasonModal = false }
		)
	}

	if (showExtendInfoModal) {
		val endDay = LocalDate.parse(orderDetailsResponse?.pickupByDate)
			.format(DateTimeFormatter.ofPattern("LLL dd, yyyy"))
		val extendDay = LocalDate.parse(orderDetailsResponse?.pickupByDate).plusDays(5L)
			.format(DateTimeFormatter.ofPattern("LLL dd, yyyy"))
		MultiOptionModal(
			title = stringResource(id = R.string.info_modal_order_extend_pickup_title),
			subTitle = stringResource(
				id = R.string.info_modal_order_extend_pickup_message,
				endDay,
				extendDay
			),
			buttons = listOf(CONFIRM, BACK),
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

	if (showEditLocationModal) {
		val binNameIndex = currentHoldingLocation.indexOf(string = "bin", ignoreCase = true)
		val locationArea = if (binNameIndex >= 0) {
			currentHoldingLocation.removeRange(binNameIndex..<currentHoldingLocation.length)
		} else {
			currentHoldingLocation
		}
		EditLocation(
			onDismissRequest = {
				showEditLocationModal = false
				resetScanLocationState()
			},
			scanLocationState = scanLocationState,
			onLocationChange = { id, holding ->
				currentHoldingLocation = holding
				onLocationChange(id, holding)
			},
			scanManager = scanManager,
			holdingAreas = holdingAreas,
			containers = orderDetailsResponse?.fulfillmentRequestDetail?.containers,
			holdingLocation = locationArea,
			subfulfillmentType = orderType
		)
	}
}

@Composable
fun AgedCancel(
	modifier: Modifier = Modifier,
	onAgedCancel: () -> Unit = {}
) {
	Column(modifier = modifier) {
		Text(
			modifier = Modifier
				.background(BrandColor.ORANGE_50)
				.padding(horizontal = 16.dp, vertical = 12.dp),
			text = stringResource(id = R.string.aged_cancel_info),
			fontFamily = FontFamily.ARCHIVO,
			fontSize = 12.sp,
			fontWeight = FontWeight(400),
			letterSpacing = 0.5.sp
		)

		PrimaryButton(
			modifier = Modifier
				.align(Alignment.CenterHorizontally)
				.fillMaxWidth(0.7f)
				.padding(vertical = 8.dp),
			text = stringResource(id = R.string.aged_cancel),
			onButtonClick = { onAgedCancel() }
		)
	}
}

@Composable
private fun CancelConfirmationModal(
	onConfirm: () -> Unit,
	onCancel: () -> Unit,
	dismissModal: () -> Unit
) {
	val context = LocalContext.current
	MultiOptionModal(
		title = stringResource(id = R.string.cancel_confirmation_title),
		subTitle = stringResource(id = R.string.cancel_confirmation_info),
		buttons = listOf(
			stringResource(id = R.string.cancel_order),
			stringResource(id = R.string.back)
		),
		buttonClick = { buttonText ->
			if (buttonText == context.getString(R.string.cancel_order)) {
				onConfirm()
			} else {
				onCancel()
			}
			dismissModal()
		},
		crossIconClick = { dismissModal() },
		onDismissRequest = { dismissModal() }
	)
}

@Composable
private fun CancelReasonModal(
	cancelReasons: List<String>,
	onCancelReasonClicked: (String) -> Unit,
	dismissModal: () -> Unit
) {
	MultiOptionModal(
		title = stringResource(id = R.string.cancel_reason_title),
		subTitle = null,
		buttons = cancelReasons,
		buttonClick = { buttonText ->
			onCancelReasonClicked(buttonText)
			dismissModal()
		},
		crossIconClick = { dismissModal() },
		onDismissRequest = { dismissModal() }
	)
}

@PreviewPdt
@Composable
fun OrderDetailsPreview() {
	OrderDetailsScreen(
		viewState = GenericViewState.Success,
		startPickupViewState = GenericViewState.Idle,
		athleteName = "Erica Franco",
		athleteProxyName = "Moyses Franco",
		athletePhoneNumber = "5087695491",
		orderNumber = "10000023",
		orderDate = "",
		expectedDate = "",
		receivedDate = "",
		packedOnDate = "",
		holdingLocation = "Main Holding Area Bin 7",
		orderDetailsResponse = OrderDetailsResponse(
			orderNumber = "10000023",
			packedOnDate = "",
			packedByUserId = "dks0523923",
			orderStatusText = "Aged",
			athleteDetail = AthleteDetail(
				athleteFirstName = "Erica",
				athleteLastName = "Franco",
				athleteProxyFirstName = "Moyses",
				athleteProxyLastName = "Franco",
				athletePhoneNumber = "5087695491"
			),
			fulfillmentRequestDetail = FulfillmentRequestDetail(
				fulfillmentRequestNumber = "10000023.001",
				subFulfillmentType = "BOPIS",
				holdingLocation = "Main Holding Area Bin 1"
			)
		),
		declineModalOptions = listOf(
			DeclineCode(id = "CUSTOMER_REQUEST", displayName = "CUSTOMER REQUEST"),
			DeclineCode(id = "DAMAGE", displayName = "DAMAGE")
		),
		onStartPickup = {},
		onPickupExtend = {},
		onPickupRemoveCheckIn = {},
		onStartPickupCompletion = {},
		ipPrefix = "",
		printer = Printer(printerName = "BOPIS", ipAddress = "", connectionStatus = false),
		onPrintHoldSlip = {},
		onPrintHoldSlipSuccessCallBack = {},
		onResetPrintHoldSlip = {},
		onConnectPrinter = { _, _ -> },
		onResetPrinter = {},
		onCancelOrder = { _, _, _ -> },
		cancelReasonData = CancelReasonData(),
		onCancelComplete = {},
		onCancelAgedError = { _ -> },
		resetScanLocationState = {},
		onLocationChange = { _, _ -> },
		onPackOrder = {},
		scanManager = null,
		holdingAreas = emptyList()
	)
}

@Preview
@Composable
fun CancelConfirmationModalPreview() {
	CancelConfirmationModal(
		onConfirm = {},
		onCancel = {},
		dismissModal = {}
	)
}

@Preview
@Composable
fun CancelReasonModalPreview() {
	CancelReasonModal(
		cancelReasons = listOf("ATHLETE REQUEST", "DAMAGE"),
		onCancelReasonClicked = {},
		dismissModal = {}
	)
}
