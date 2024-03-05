package com.nextuple.nsf.ui.screen.prep

import android.os.Handler
import android.os.Looper
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
import androidx.navigation.NavController
import com.nextuple.nsf.ui.nav.Screen
import com.nextuple.nsf.ui.state.ConfigViewModel
import com.nextuple.nsf.ui.state.InfoViewModel
import com.nextuple.nsf.ui.state.PrepOrderViewModel
import com.nextuple.nsf.ui.state.PrinterName
import com.nextuple.nsf.ui.state.SettingsViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.SubFulfillmentType

@Composable
fun PrepOrderScreen(
    scanManager: ScanManager,
    prepOrderViewModel: PrepOrderViewModel = hiltViewModel(),
    settingsVM: SettingsViewModel,
    infoVM: InfoViewModel,
    configVM: ConfigViewModel,
    navCtrl: NavController,
    packType: PackType,
    data: String, // todo: Rename to add detail
    onResetPrepStage: () -> Unit // TODO: Refactor this away when InfoVM, PrepVM, and PrepOrderVM are fully decoupled.
) {
	val uiState = prepOrderViewModel.prepOrderUiData.state
	val prepOrder = prepOrderViewModel.prepOrderUiData.prepOrder

	LaunchedEffect(Unit) {
		prepOrderViewModel.setUiData(packType = packType, data = data)
	}

	fun recordHoldLocation(holdingLocation: String) {
		prepOrderViewModel.stageTask?.containers?.firstOrNull()?.id?.let { it1 ->
			prepOrderViewModel.recordHoldingLocation(it1, holdingLocation)
		}
	}

	when {
		uiState == GenericViewState.Idle || uiState == GenericViewState.Loading -> {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator(
					color = BrandColor.GRAY_900
				)
			}
		}
		uiState == GenericViewState.Success && prepOrder != null -> {
			if (prepOrder.subFulfillmentType == SubFulfillmentType.BOPL.name) {
				BOPLPrepDetailsScreen(
					prepOrder = prepOrder,
					printer = settingsVM.findPrinter(PrinterName.BOPL),
					onPackAndGetHoldSlip = {
						prepOrderViewModel.packAndGetHoldSlip()
					},
					onDisConnectPrinter = settingsVM::disConnectPrinter,
					resetScreen = {
						prepOrderViewModel.resetHoldSlipState()
					},
					holdSlipState = prepOrderViewModel.holdSlipState,
					currentPrepStage = prepOrderViewModel.currentPrepStage.value,
					onRecordHoldingLocation = ::recordHoldLocation,
					ipPrefix = settingsVM.ipPrefix,
					printerConnectionState = settingsVM.printerConnectionState,
					onConnectPrinter = settingsVM::connectPrinter,
					onResetPrinter = {
						settingsVM.resetConnectionState()
					},
					onStageCompletionCallBack = {
						onResetPrepStage()
						infoVM.getStoreOverview()
						prepOrderViewModel.resetHoldLocationState()
						settingsVM.resetHoldSlipPrintState()
						prepOrderViewModel.resetHoldSlipState()
						navCtrl.navigate(Screen.PREP.route) {
							popUpTo(Screen.HOME.route)
						}
					},
					holdingAreas = configVM.getHoldingLocations(),
					printerList = settingsVM.printersList,
					onPrintHoldSlip = {
						prepOrderViewModel.stageTask?.holdSlipZPL?.let { settingsVM.printBOPISHoldSlip(it) }
					},
					scanManager = scanManager,
					onPackItem = {
						prepOrderViewModel.packItem(it)
					}
				)
			} else {
				BOPISPrepDetailScreen(
					scanManager = scanManager,
					isOrderAssembled = false,
					prepOrder = prepOrder,
					holdingAreas = configVM.getHoldingLocations(),
					currentPrepStage = prepOrderViewModel.currentPrepStage.value,
					holdSlipState = prepOrderViewModel.holdSlipState,
					holdLocationState = prepOrderViewModel.holdLocationState,
					onPackItem = {
						prepOrderViewModel.packItem(it)
					},
					onPackOrder = {
						prepOrderViewModel.packAndGetHoldSlip()
					},
					onRecordHoldingLocation = ::recordHoldLocation,
					ipPrefix = settingsVM.ipPrefix,
					printer = settingsVM.findPrinter(PrinterName.BOPIS),
					printerConnectionState = settingsVM.printerConnectionState,
					onConnectPrinter = settingsVM::connectPrinter,
					onResetPrinter = {
						settingsVM.resetConnectionState()
					},
					onPrintHoldSlip = {
						prepOrderViewModel.stageTask?.holdSlipZPL?.let { settingsVM.printBOPISHoldSlip(it) }
					},
					onStageCompletionCallBack = {
						// TODO: Clean up dependencies on shared viewmodels
						infoVM.getStoreOverview()
						navCtrl.navigate(Screen.PREP.route) {
							popUpTo(Screen.HOME.route)
						}
						Handler(Looper.getMainLooper()).postDelayed({
							onResetPrepStage()
							prepOrderViewModel.resetHoldLocationState()
							settingsVM.resetHoldSlipPrintState()
							prepOrderViewModel.resetHoldSlipState()
						}, 1000)
					}
				)
			}
		}
		// Stay on prep screen if UPC Is not found by PackByGear
		uiState == GenericViewState.Failure && packType == PackType.GEAR -> {
			Toast.makeText(
				LocalContext.current,
				"Upc not found in pack orders.",
				Toast.LENGTH_SHORT
			).show()
			navCtrl.navigate(Screen.PREP.route) {
				popUpTo(Screen.HOME.route)
			}
		}
		// Fix failure nav issue
		uiState == GenericViewState.Failure ||
			(uiState == GenericViewState.Success && prepOrder == null) -> {
			// TODO: Replace with standard error when design is available.
			// TODO: Restore when info, prep, and preo order VMs are decoupled.
// 			Toast.makeText(
// 				LocalContext.current,
// 				"Failure retrieving order to prep.",
// 				Toast.LENGTH_SHORT
// 			).show()
			navCtrl.navigate(Screen.PREP.route) {
				popUpTo(Screen.HOME.route)
			}
		}
	}
}
