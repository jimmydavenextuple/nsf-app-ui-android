package com.nextuple.nsf.ui.screen.prep

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.ui.component.PrinterModal
import com.nextuple.nsf.ui.screen.prep.component.PackOrderCard
import com.nextuple.nsf.ui.screen.prep.component.PrepDetailScaffold
import com.nextuple.nsf.ui.screen.prep.component.ScanAndPackUnitsCard
import com.nextuple.nsf.ui.screen.prep.component.ScanLocationCard
import com.nextuple.nsf.ui.screen.prep.component.SelectHoldingAreaCard
import com.nextuple.nsf.ui.state.PrepViewModel
import com.nextuple.nsf.ui.state.PrepViewModel.PrepOrder
import com.nextuple.nsf.ui.state.PrintViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.AppMessage
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.Printer
import com.nextuple.nsf.ui.util.ScanManager
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class PrepStep {
	PACK, HOLDING_AREA, SCAN_LOCATION
}

@Composable
fun BOPISPrepDetailScreen(
	printViewModel: PrintViewModel,
	scanManager: ScanManager,
	currentPrepStage: PrepViewModel.Step,
	prepOrder: PrepOrder,
	isOrderAssembled: Boolean,
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
	onPackOrder: () -> Unit = {},
	onPrintHoldSlip: () -> Unit = {}
) {
	val context = LocalContext.current
	var showConnectModal by remember { mutableStateOf(false) }
	var selectedHoldingArea by remember { mutableStateOf("") }

	// check active step using pack task status
	var activeStep by remember {
		mutableStateOf(
			if (currentPrepStage == PrepViewModel.Step.Stage) {
				PrepStep.HOLDING_AREA
			} else {
				PrepStep.PACK
			}
		)
	}

	fun updateStep(prepStep: PrepStep) {
		activeStep = prepStep
	}

	fun isStepActive(prepStep: PrepStep) = activeStep == prepStep
	fun isStepComplete(prepStep: PrepStep) = activeStep.ordinal > prepStep.ordinal

	fun handlePrintHoldSlip() {
		if (printer.connectionStatus) {
			onPrintHoldSlip()
			if (isStepActive(PrepStep.PACK)) {
				updateStep(PrepStep.HOLDING_AREA)
			}
		} else {
			showConnectModal = true
		}
	}

	// Handling Pack scan and bin scan
	DisposableEffect(Unit) {
		scanManager.set { data, _ ->
			if (activeStep == PrepStep.PACK) {
				onPackItem(data)
			} else if (activeStep == PrepStep.SCAN_LOCATION) {
				if (!data.lowercase().contains("bin")) {
					Toast.makeText(context, "", Toast.LENGTH_LONG).show()
					return@set
				}

				onRecordHoldingLocation("$selectedHoldingArea $data")
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
		if (isOrderAssembled) {
			PackOrderCard(
				isActive = isStepActive(PrepStep.PACK),
				isComplete = isStepComplete(PrepStep.PACK),
				onPackOrder = onPackOrder
			)
		} else {
			ScanAndPackUnitsCard(
				packItems = prepOrder.packItems,
				stepNumber = "1",
				isActive = isStepActive(PrepStep.PACK),
				isComplete = isStepComplete(PrepStep.PACK),
				onPackItem = onPackItem,
				pickedBy = prepOrder.pickedBy,
				onAllItemsScanned = onPackOrder,
				onStageCompletionCallBack = onStageCompletionCallBack,
				onConfirmDecline = onConfirmDecline
			)
		}
		SelectHoldingAreaCard(
			stepNumber = "2",
			isActive = isStepActive(PrepStep.HOLDING_AREA),
			isComplete = isStepComplete(PrepStep.HOLDING_AREA),
			holdingAreas = holdingAreas,
			selectedHoldingArea = selectedHoldingArea,
			onSelectedOptionTextChanged = { selectedHoldingArea = it },
			onSubmitHoldingArea = { updateStep(PrepStep.SCAN_LOCATION) },
			isReprintActive = true,
			onReprintHoldSlip = { handlePrintHoldSlip() }
		)
		ScanLocationCard(
			isActive = isStepActive(PrepStep.SCAN_LOCATION),
			holdLocationState = holdLocationState,
			stepNumber = "3",
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

	when {
		getHoldSlipState == GenericViewState.Loading -> {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator(
					color = BrandColor.GRAY_900
				)
			}
		}

		getHoldSlipState == GenericViewState.Success && isStepActive(PrepStep.PACK) -> {
			handlePrintHoldSlip()
		}
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
private fun BOPISPrepDetailScreenPreview() {
	BOPISPrepDetailScreen(
		printViewModel = hiltViewModel(),
		scanManager = NoOpScanManager(),
		isOrderAssembled = false,
		prepOrder = PrepOrder(
			athleteName = "Athlete",
			orderNumber = "1010101010",
			pickedBy = "Picker",
			subFulfillmentType = "",
			packItems = listOf(packTaskItem, packTaskItem)
		),
		holdingAreas = emptyList(),
		ipPrefix = "",
		printer = Printer(printerName = "BOPIS", ipAddress = "", connectionStatus = false),
		onConnectPrinter = { _, _ -> },
		onResetPrinter = {},
		currentPrepStage = PrepViewModel.Step.Pack,
		onConfirmDecline = { _, _, _ -> },
		holdLocationState = GenericViewState.Idle
	)
}
