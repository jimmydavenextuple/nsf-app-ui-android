package com.nextuple.nsf.ui.screen.prep

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.ui.component.PrinterModal
import com.nextuple.nsf.ui.screen.prep.component.PrepDetailScaffold
import com.nextuple.nsf.ui.screen.prep.component.ScanAndPackUnitsCard
import com.nextuple.nsf.ui.screen.prep.component.ScanLocationCard
import com.nextuple.nsf.ui.screen.prep.component.ScanPackageCard
import com.nextuple.nsf.ui.screen.prep.component.SelectHoldingAreaCard
import com.nextuple.nsf.ui.state.PrepViewModel
import com.nextuple.nsf.ui.state.PrintViewModel
import com.nextuple.nsf.ui.util.AppMessage
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.Printer
import com.nextuple.nsf.ui.util.ScanManager
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class SDDPrepStep {
	PACK, SCAN_PACKAGE, HOLDING_AREA, SCAN_LOCATION
}

@Composable
fun SDDPrepDetailsScreen(
	printViewModel: PrintViewModel,
	scanManager: ScanManager,
	currentPrepStage: PrepViewModel.Step,
	prepOrder: PrepViewModel.PrepOrder,
	onPackItem: (String) -> Boolean = { false },
	getHoldSlipState: GenericViewState = GenericViewState.Idle,
	holdLocationState: GenericViewState = GenericViewState.Idle,
	onRecordHoldingLocation: (holdingLocation: String) -> Unit = { _ -> },
	onStageCompletionCallBack: () -> Unit = {},
	onConfirmDecline: (declineReason: String, index: Int, item: PackTaskItem) -> Unit,
	holdingAreas: List<String>,
	ipPrefix: String?,
	printer: Printer,
	printerConnectionState: GenericViewState = GenericViewState.Idle,
	onConnectPrinter: (Printer, String) -> Unit,
	onResetPrinter: () -> Unit,
	onPackOrder: (String) -> Unit = {},
	onPrintHoldSlip: () -> Unit = {}
) {
	val context = LocalContext.current
	var showConnectModal by remember { mutableStateOf(false) }
	var selectedHoldingArea by remember { mutableStateOf("") }

	// check active step using pack task status
	var activeStep by remember {
		mutableStateOf(
			if (currentPrepStage == PrepViewModel.Step.Stage) {
				SDDPrepStep.HOLDING_AREA
			} else {
				SDDPrepStep.PACK
			}
		)
	}

	fun updateStep(prepStep: SDDPrepStep) {
		activeStep = prepStep
	}

	fun isStepActive(prepStep: SDDPrepStep) = activeStep == prepStep
	fun isStepComplete(prepStep: SDDPrepStep) = activeStep.ordinal > prepStep.ordinal

	fun handlePrintHoldSlip() {
		if (printer.connectionStatus) {
			onPrintHoldSlip()
			if (isStepActive(SDDPrepStep.SCAN_PACKAGE)) {
				updateStep(SDDPrepStep.HOLDING_AREA)
			}
		} else {
			showConnectModal = true
		}
	}

	// Handling Pack scan and bin scan
	DisposableEffect(Unit) {
		scanManager.set { data, _ ->
			when (activeStep) {
				SDDPrepStep.PACK -> {
					onPackItem(data)
				}

				SDDPrepStep.SCAN_PACKAGE -> {
					onPackOrder(data)
				}

				SDDPrepStep.SCAN_LOCATION -> {
					if (!data.lowercase().contains("bin")) {
						Toast.makeText(context, "", Toast.LENGTH_LONG).show()
						return@set
					}

					onRecordHoldingLocation("$selectedHoldingArea $data")
				}

				SDDPrepStep.HOLDING_AREA -> { /* No Scan Action */
				}
			}
		}

		onDispose {
			scanManager.set { _, _ -> }
		}
	}

	var appMessage: AppMessage? by remember { mutableStateOf(null) }
	fun setOmniMessage(updatedMessage: AppMessage?) {
		appMessage = updatedMessage
	}
	PrepDetailScaffold(
		athleteName = prepOrder.athleteName,
		orderNumber = prepOrder.orderNumber,
		appMessage = appMessage,
		onDismissMessage = {
			setOmniMessage(null)
		}
	) {
		// TODO: order assembled??
		ScanAndPackUnitsCard(
			packItems = prepOrder.packItems,
			stepNumber = "1",
			isActive = isStepActive(SDDPrepStep.PACK),
			isComplete = isStepComplete(SDDPrepStep.PACK),
			onPackItem = onPackItem,
			pickedBy = prepOrder.pickedBy,
			onStageCompletionCallBack = onStageCompletionCallBack,
			onConfirmDecline = onConfirmDecline,
			onAllItemsScanned = { updateStep(SDDPrepStep.SCAN_PACKAGE) }
		)
		ScanPackageCard(
			isStepActive = isStepActive(SDDPrepStep.SCAN_PACKAGE),
			isComplete = isStepComplete(SDDPrepStep.SCAN_PACKAGE),
			holdSlipState = getHoldSlipState,
			onScanClick = {
				if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
					onPackOrder("Test")
				}
			}
		)

		SelectHoldingAreaCard(
			stepNumber = "3",
			isActive = isStepActive(SDDPrepStep.HOLDING_AREA),
			isComplete = isStepComplete(SDDPrepStep.HOLDING_AREA),
			holdingAreas = holdingAreas,
			selectedHoldingArea = selectedHoldingArea,
			onSelectedOptionTextChanged = { selectedHoldingArea = it },
			onSubmitHoldingArea = { updateStep(SDDPrepStep.SCAN_LOCATION) },
			isReprintActive = true,
			onReprintHoldSlip = { handlePrintHoldSlip() }
		)
		ScanLocationCard(
			stepNumber = "4",
			isActive = isStepActive(SDDPrepStep.SCAN_LOCATION),
			holdLocationState = holdLocationState,
			onScanClick = {
				if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
					val bin = "Bin ${Random.nextInt(from = 1, until = 100)}"

					onRecordHoldingLocation("$selectedHoldingArea $bin")
				}
			}
		)
	}

	if (holdLocationState is GenericViewState.Success) {
		LaunchedEffect(Unit) {
			delay(1000)
			onStageCompletionCallBack()
		}
	}

	if (showConnectModal) {
		PrinterModal(
			ipPrefix = ipPrefix,
			printer = printer,
			onReset = onResetPrinter,
			printerConnectionState = printerConnectionState,
			toggleModal = { showConnectModal = it },
			onConnectPrinter = onConnectPrinter
		)
	}

	if (getHoldSlipState == GenericViewState.Success && isStepActive(SDDPrepStep.SCAN_PACKAGE)) {
		LaunchedEffect(Unit) {
			delay(1000)
			handlePrintHoldSlip()
		}
	} else if (getHoldSlipState == GenericViewState.Failure) {
		setOmniMessage(AppMessage.getHoldSlipError)
	}

	LaunchedEffect(getHoldSlipState) {
		if (getHoldSlipState == GenericViewState.Failure) {
			setOmniMessage(AppMessage.getHoldSlipError)
		}
	}

	LaunchedEffect(printViewModel.printHoldSlipState) {
		if (printViewModel.printHoldSlipState == GenericViewState.Failure) {
			setOmniMessage(AppMessage.printHoldSlipError)
		}
	}
}

@PreviewPdt
@Composable
private fun SDDPrepDetailScreenPreview() {
	SDDPrepDetailsScreen(
		printViewModel = hiltViewModel(),
		scanManager = NoOpScanManager(),
		prepOrder = PrepViewModel.PrepOrder(
			athleteName = "Customer",
			orderNumber = "1010101010",
			pickedBy = "Picker",
			subFulfillmentType = "",
			packItems = listOf(packTaskItem, packTaskItem)
		),
		holdingAreas = emptyList(),
		ipPrefix = "",
		printer = Printer(printerName = "SDD", ipAddress = "", connectionStatus = false),
		onConnectPrinter = { _, _ -> },
		onResetPrinter = {},
		currentPrepStage = PrepViewModel.Step.Pack,
		onConfirmDecline = { _, _, _ -> },
		holdLocationState = GenericViewState.Idle
	)
}
