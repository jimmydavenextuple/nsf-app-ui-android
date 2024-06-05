package com.nextuple.nsf.ui.screen.prep

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextuple.nsf.ui.state.ConfigViewModel
import com.nextuple.nsf.ui.state.PrepViewModel
import com.nextuple.nsf.ui.state.PrintViewModel
import com.nextuple.nsf.ui.state.SettingsViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PrinterName
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.SubFulfillmentType

enum class PrepScreenTab(val displayName: String) {
	PACK("PACK"),
	STAGE("STAGE")
}

@Composable
fun PrepScreen(
	prepViewModel: PrepViewModel = hiltViewModel(),
	printViewModel: PrintViewModel = hiltViewModel(),
	configVM: ConfigViewModel,
	settingsVM: SettingsViewModel,
	scanManager: ScanManager,
	frToPack: String?,
	numPackTasks: Int,
	onClickPackByOrder: () -> Unit,
	resetScreen: () -> Unit
) {
	val ctx = LocalContext.current
	val prepOrder = prepViewModel.prepOrder

	fun onScanGear(upc: String) {
		prepViewModel.fetchPrepOrder(packType = PackType.GEAR, upcOrFrNo = upc)
	}

	fun onPrintHoldSlip(printerName: PrinterName) {
		prepViewModel.stageTask?.holdSlipZPL?.let {
			val printer = settingsVM.findPrinter(printerName)
			printViewModel.printHoldSlip(it, printer, settingsVM.bypassPrinter)
		}
	}

	fun onPrintBoplHoldSlip() {
		prepViewModel.prepOrder?.holdSlipZPL?.let {
			val printer = settingsVM.findPrinter(PrinterName.BOPL)
			printViewModel.printHoldSlip(it, printer, settingsVM.bypassPrinter)
		}
	}

	fun recordHoldLocation(holdingLocation: String) {
		prepViewModel.stageTask?.containers?.firstOrNull()?.id?.let {
			prepViewModel.recordHoldingLocation(it, holdingLocation)
		}
	}

	fun onStageCompletionCallBack() {
		prepViewModel.resetHoldLocationState()
		prepViewModel.resetGetHoldSlipState()
		prepViewModel.resetPrepState()
		resetScreen()
	}

	fun onFailure() {
		prepViewModel.resetPrepState()
	}

	LaunchedEffect(Unit) {
		if (!frToPack.isNullOrEmpty()) {
			prepViewModel.fetchPrepOrder(packType = PackType.ORDER, upcOrFrNo = frToPack)
		} else {
			prepViewModel.fetchCurrentStep()
		}
	}

	when (prepViewModel.viewState) {
		GenericViewState.Loading -> {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator(color = BrandColor.GRAY_900)
			}
		}

		GenericViewState.Failure -> {
			LaunchedEffect(Unit) {
				Toast.makeText(
					ctx,
					"Failure retrieving user prep tasks.",
					Toast.LENGTH_SHORT
				).show()
				onFailure()
			}
		}

		else -> when (prepViewModel.currentStep) {
			PrepViewModel.Step.Landing -> {
				PrepLandingScreen(
					numPackTasks = numPackTasks,
					scanManager = scanManager,
					onScanGear = ::onScanGear,
					onClickPackByOrder = onClickPackByOrder,
					resetScreen = resetScreen
				)
			}

			else -> when (prepOrder?.subFulfillmentType) {
				SubFulfillmentType.BOPL.name -> {
					BOPLPrepDetailsScreen(
						printViewModel = printViewModel,
						prepOrder = prepOrder,
						printer = settingsVM.findPrinter(PrinterName.BOPL),
						startPackAndGetHoldSlip = prepViewModel::startPackAndGetHoldSlip,
						completePack = prepViewModel::completePack,
						onDisConnectPrinter = settingsVM::disConnectPrinter,
						resetScreen = prepViewModel::resetGetHoldSlipState,
						startPackAndGetHoldSlipState = prepViewModel.startPackAndGetHoldSlipState,
						currentPrepStage = prepViewModel.currentStep,
						onRecordHoldingLocation = ::recordHoldLocation,
						ipPrefix = settingsVM.ipPrefix,
						printerConnectionState = settingsVM.printerConnectionState,
						onConnectPrinter = settingsVM::connectPrinter,
						onResetPrinter = settingsVM::resetConnectionState,
						onStageCompletionCallBack = ::onStageCompletionCallBack,
						holdingAreas = configVM.getHoldingLocations(),
						printerList = settingsVM.printersList,
						onPrintHoldSlip = { onPrintBoplHoldSlip() },
						scanManager = scanManager,
						onPackItem = prepViewModel::packItem
					)
				}

				SubFulfillmentType.BOPIS.name -> {
					BOPISPrepDetailScreen(
						printViewModel = printViewModel,
						scanManager = scanManager,
						isOrderAssembled = false,
						prepOrder = prepOrder,
						holdingAreas = configVM.getHoldingLocations(),
						currentPrepStage = prepViewModel.currentStep,
						getHoldSlipState = prepViewModel.getHoldSlipState,
						holdLocationState = prepViewModel.holdLocationState,
						onPackItem = prepViewModel::packItem,
						onPackOrder = prepViewModel::packAndGetHoldSlip,
						onRecordHoldingLocation = ::recordHoldLocation,
						ipPrefix = settingsVM.ipPrefix,
						printer = settingsVM.findPrinter(PrinterName.BOPIS),
						printerConnectionState = settingsVM.printerConnectionState,
						onConnectPrinter = settingsVM::connectPrinter,
						onResetPrinter = settingsVM::resetConnectionState,
						onPrintHoldSlip = { onPrintHoldSlip(PrinterName.BOPIS) },
						onStageCompletionCallBack = ::onStageCompletionCallBack,
						onConfirmDecline = { declineReason, index, itemToDecline ->
							prepViewModel.declinePackItem(declineReason, index, itemToDecline)
						}
					)
				}

				SubFulfillmentType.SAME_DAY.name -> {
					SDDPrepDetailsScreen(
						printViewModel = printViewModel,
						scanManager = scanManager,
						prepOrder = prepOrder,
						holdingAreas = configVM.getHoldingLocations(),
						currentPrepStage = prepViewModel.currentStep,
						getHoldSlipState = prepViewModel.getHoldSlipState,
						holdLocationState = prepViewModel.holdLocationState,
						onPackItem = prepViewModel::packItem,
						onPackOrder = {
							// Print placeholder for passing through package data
							println("aaa PackageData: $it")
							prepViewModel.packAndGetHoldSlip()
						},
						onRecordHoldingLocation = ::recordHoldLocation,
						ipPrefix = settingsVM.ipPrefix,
						printer = settingsVM.findPrinter(PrinterName.SDD),
						printerConnectionState = settingsVM.printerConnectionState,
						onConnectPrinter = settingsVM::connectPrinter,
						onResetPrinter = settingsVM::resetConnectionState,
						onPrintHoldSlip = { onPrintHoldSlip(PrinterName.SDD) },
						onStageCompletionCallBack = ::onStageCompletionCallBack,
						onConfirmDecline = { declineReason, index, itemToDecline ->
							prepViewModel.declinePackItem(declineReason, index, itemToDecline)
						}
					)
				}

				else -> {
					LaunchedEffect(Unit) {
						Toast.makeText(
							ctx,
							"Failure retrieving order to prep.",
							Toast.LENGTH_SHORT
						).show()
						onFailure()
					}
				}
			}
		}
	}
}
