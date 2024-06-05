package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.MultiOptionModal
import com.nextuple.nsf.ui.component.PrinterBOPLModal
import com.nextuple.nsf.ui.component.PrinterModal
import com.nextuple.nsf.ui.screen.prep.BOPLPrepStep.PLACE_HOLD_SLIP
import com.nextuple.nsf.ui.screen.prep.BOPLPrepStep.PRINT_HOLD_SLIP
import com.nextuple.nsf.ui.screen.prep.BOPLPrepStep.SELECT_HOLDING
import com.nextuple.nsf.ui.screen.prep.component.MatchHoldSlipsCard
import com.nextuple.nsf.ui.screen.prep.component.PrepDetailScaffold
import com.nextuple.nsf.ui.screen.prep.component.PrintHoldSlipCard
import com.nextuple.nsf.ui.screen.prep.component.SelectHoldingAreaCard
import com.nextuple.nsf.ui.state.PrepViewModel
import com.nextuple.nsf.ui.state.PrepViewModel.PrepOrder
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Pack
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Stage
import com.nextuple.nsf.ui.state.PrintViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.AppMessage
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.Printer
import com.nextuple.nsf.ui.util.ScanManager
import kotlinx.coroutines.delay

enum class BOPLPrepStep {
	PRINT_HOLD_SLIP, PLACE_HOLD_SLIP, SELECT_HOLDING
}

@Composable
fun BOPLPrepDetailsScreen(
	printViewModel: PrintViewModel,
	prepOrder: PrepOrder,
	ipPrefix: String?,
	printer: Printer,
	printerList: List<Printer>?,
	onConnectPrinter: (Printer, String) -> Unit,
	currentPrepStage: PrepViewModel.Step,
	printerConnectionState: GenericViewState = GenericViewState.Idle,
	onResetPrinter: () -> Unit,
	startPackAndGetHoldSlip: () -> Unit,
	startPackAndGetHoldSlipState: GenericViewState = GenericViewState.Idle,
	completePack: () -> Unit,
	onPrintHoldSlip: () -> Unit,
	onDisConnectPrinter: (Printer) -> Unit,
	resetScreen: () -> Unit,
	holdingAreas: List<String>,
	onRecordHoldingLocation: (holdingLocation: String) -> Unit,
	onStageCompletionCallBack: () -> Unit,
	scanManager: ScanManager,
	onPackItem: (String) -> Boolean
) {
	var selectedHoldingArea by remember { mutableStateOf("") }
	var showConnectModal by remember { mutableStateOf(false) }
	var showConnectBOPLModal by remember { mutableStateOf(false) }
	val isMultiUnit = (prepOrder.packItems.size) > 1
	var currentStep by remember {
		mutableStateOf(
			when (currentPrepStage) {
				Stage -> SELECT_HOLDING
				Pack -> PLACE_HOLD_SLIP
				else -> PRINT_HOLD_SLIP
			}
		)
	}

	fun isPrintActive(): Boolean = currentStep == PRINT_HOLD_SLIP
	fun isPlaceActive(): Boolean = currentStep == PLACE_HOLD_SLIP
	fun isSelectActive(): Boolean = currentStep == SELECT_HOLDING

	fun updateStep(prepStep: BOPLPrepStep) {
		currentStep = prepStep
	}

	var showDisconnectModal by remember { mutableStateOf(false) }
	var showAcceptedScreen by remember { mutableStateOf(false) }
	var showMultiUnitModal by remember { mutableStateOf(false) }
	var isStepNextActive by remember { mutableStateOf(false) }
	var isPrinted by remember { mutableStateOf(false) }
	fun handlePrintHoldSlip() {
		if (printer.connectionStatus) {
			onPrintHoldSlip()
		} else {
			showConnectModal = true
		}
	}

	DisposableEffect(Unit) {
		scanManager.set { data, _ ->
			if (isPlaceActive() && isMultiUnit && !showMultiUnitModal) {
				isStepNextActive = false
				showMultiUnitModal = true
			} else if (showMultiUnitModal) {
				if (onPackItem(data)) {
					isStepNextActive = true
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
	if (!showAcceptedScreen) {
		PrepDetailScaffold(
			athleteName = prepOrder.athleteName,
			orderNumber = prepOrder.orderNumber,
			appMessage = appMessage,
			onDismissMessage = { setOmniMessage(null) }
		) {
			PrintHoldSlipCard(
				isActive = isPrintActive(),
				printer = printer,
				onPrintHoldSlipEvent = {
					startPackAndGetHoldSlip()
					updateStep(PLACE_HOLD_SLIP)
				},
				showPrinterModalEvent = {
					showConnectBOPLModal = true
				},
				onDisConnectPrinter = {
					showDisconnectModal = true
				},
				packItems = prepOrder.packItems,
				isMultiUnit = isMultiUnit
			)

			MatchHoldSlipsCard(
				isPlaceActive = isPlaceActive(),
				isSelectActive = isSelectActive(),
				isMultiUnit = isMultiUnit,
				packItems = prepOrder.packItems,
				athlete = prepOrder.athleteName,
				onReprintHoldSlip = { handlePrintHoldSlip() },
				onUpdateStep = {
					completePack()
					updateStep(SELECT_HOLDING)
				},
				onToggleMultiUnitModal = {
					isStepNextActive = false
					showMultiUnitModal = true
				}
			)

			SelectHoldingAreaCard(
				stepNumber = "3",
				isActive = isSelectActive(),
				holdingAreas = holdingAreas,
				selectedHoldingArea = selectedHoldingArea,
				isReprintActive = false,
				onReprintHoldSlip = {},
				onSelectedOptionTextChanged = { selectedHoldingArea = it },
				onSubmitHoldingArea = {
					onRecordHoldingLocation(selectedHoldingArea)
					showAcceptedScreen = true
				},
				isComplete = false
			)
		}
	} else {
		HoldingLocation(holdingArea = selectedHoldingArea)
		LaunchedEffect(Unit) {
			delay(1000)
			onStageCompletionCallBack()
		}
	}
	if (showConnectBOPLModal) {
		PrinterBOPLModal(
			printersList = printerList,
			connectPrinter = onConnectPrinter,
			otherPrinterOnClick = {
				showConnectBOPLModal = false
				showConnectModal = true
			},
			onDismissRequest = {
				showConnectBOPLModal = false
				showConnectModal = false
			},
			boplPrinter = printer
		)
	}

	if (showMultiUnitModal) {
		BOPLMultiUnitModal(
			onPackItem = onPackItem,
			onDismissRequest = { showMultiUnitModal = false },
			packItems = prepOrder.packItems,
			athlete = prepOrder.athleteName,
			orderNum = prepOrder.orderNumber,
			isStep2Active = isStepNextActive,
			toggleStep2 = { isStepNextActive = true }
		)
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

	if (showDisconnectModal) {
		val yesOption = stringResource(id = R.string.yes)
		MultiOptionModal(
			title = stringResource(id = R.string.remove_printer),
			subTitle = stringResource(R.string.remove_printer_subtitle, printer.printerName),
			buttons = listOf(
				stringResource(id = R.string.yes),
				stringResource(id = R.string.no)
			),
			buttonClick = { optionSelected ->
				showDisconnectModal = false
				if (optionSelected == yesOption) {
					onDisConnectPrinter(printer)
				}
			},
			crossIconClick = { showDisconnectModal = false }
		) {
		}
	}
	if (startPackAndGetHoldSlipState == GenericViewState.Loading) {
		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				color = BrandColor.GRAY_900
			)
		}
	} else if (startPackAndGetHoldSlipState == GenericViewState.Success && isPlaceActive() && !isPrinted) {
		handlePrintHoldSlip()
		isPrinted = true
	} else if (startPackAndGetHoldSlipState == GenericViewState.Failure) {
		// Should this be the error or the error message
		InfoModal(
			modifier = Modifier.fillMaxWidth(0.95f),
			title = stringResource(id = R.string.info_modal_pick_on_the_bench_title),
			subTitle = stringResource(id = R.string.info_modal_pick_on_the_bench_message),
			buttonText = stringResource(id = R.string.ok),
			buttonClick = {
				resetScreen()
			},
			crossIconClick = {
				resetScreen()
			},
			dismissOnBackPress = false,
			dismissOnClickOutside = false,
			onDismissRequest = { }
		)
	}

	LaunchedEffect(printViewModel.printHoldSlipState) {
		if (printViewModel.printHoldSlipState == GenericViewState.Failure) {
			setOmniMessage(AppMessage.printHoldSlipError)
		}
	}
}

@Composable
fun HoldingLocation(holdingArea: String) {
	Column(
		Modifier
			.background(BrandColor.GREEN_500)
			.fillMaxHeight()
			.fillMaxWidth(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Image(
			modifier = Modifier.size(50.dp),
			painter = painterResource(id = R.drawable.ic_check_white),
			contentDescription = "Complete"
		)
		Spacer(modifier = Modifier.size(20.dp))
		Text(
			text = "Order located to",
			style = TextStyle(
				fontSize = 16.sp,
				lineHeight = 20.8.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight(400),
				color = BrandColor.WHITE,
				textAlign = TextAlign.Center,
				letterSpacing = 0.5.sp
			)
		)
		Text(
			text = holdingArea,
			style = TextStyle(
				fontSize = 16.sp,
				lineHeight = 20.8.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight(900),
				color = BrandColor.WHITE,
				textAlign = TextAlign.Center,
				letterSpacing = 0.5.sp
			)
		)
	}
}

@Composable
@PreviewPdt
private fun PreviewBOPLPackScreen() {
	BOPLPrepDetailsScreen(
		printViewModel = hiltViewModel(),
		prepOrder = PrepOrder(
			athleteName = "Mary Kate",
			orderNumber = "12345678",
			pickedBy = "Picker",
			subFulfillmentType = "",
			packItems = listOf(packTaskItem, packTaskItem)
		),
		printer = Printer("BOPL", "123", connectionStatus = true),
		onDisConnectPrinter = {},
		resetScreen = {},
		holdingAreas = emptyList(),
		onRecordHoldingLocation = { _ -> },
		onStageCompletionCallBack = {},
		currentPrepStage = Pack,
		ipPrefix = "",
		onConnectPrinter = { _, _ -> },
		onResetPrinter = {},
		printerList = null,
		onPrintHoldSlip = {},
		scanManager = NoOpScanManager(),
		onPackItem = { _ -> false },
		startPackAndGetHoldSlip = {},
		completePack = {}
	)
}

@Composable
@PreviewPdt
private fun PreviewHoldingLocationScreen() {
	HoldingLocation(holdingArea = "Main Holding Location")
}
