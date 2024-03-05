package com.nextuple.nsf.ui.screen.prep

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.TextInfo
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToAction
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.component.PrinterModal
import com.nextuple.nsf.ui.state.InfoViewModel
import com.nextuple.nsf.ui.state.PrepOrder
import com.nextuple.nsf.ui.state.Printer
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager
import kotlin.random.Random

enum class PrepStep {
	PACK, HOLDING_AREA, SCAN_LOCATION
}

@Composable
fun BOPISPrepDetailScreen(
	scanManager: ScanManager,
	currentPrepStage: InfoViewModel.PrepStage,
	prepOrder: PrepOrder,
	isOrderAssembled: Boolean,
	onPackItem: (String) -> Boolean = { false },
	holdSlipState: GenericViewState = GenericViewState.Idle,
	holdLocationState: GenericViewState = GenericViewState.Idle,
	onRecordHoldingLocation: (holdingLocation: String) -> Unit = { _ -> },
	onStageCompletionCallBack: () -> Unit = {},
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
			if (currentPrepStage == InfoViewModel.PrepStage.StageOrder) {
                PrepStep.HOLDING_AREA
			} else {
                PrepStep.PACK
			}
		)
	}

	fun updateStep(prepStep: PrepStep) { activeStep = prepStep }
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
	LaunchedEffect(Unit) {
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
	}

	Column(
		modifier = Modifier
			.background(BrandColor.GRAY_100)
	) {
		PrepHeader(
			modifier = Modifier
				.fillMaxWidth()
				.background(BrandColor.GRAY_50),
			athlete = prepOrder.athleteName,
			orderNum = prepOrder.orderNumber
		)
		Column(
			modifier = Modifier
				.padding(horizontal = 16.dp, vertical = 8.dp)
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
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
					isActive = isStepActive(PrepStep.PACK),
					isComplete = isStepComplete(PrepStep.PACK),
					onPackItem = onPackItem,
					pickedBy = prepOrder.pickedBy,
					onPackOrder = onPackOrder
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
				onScanClick = {
					if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
						val bin = "Bin ${Random.nextInt(from = 1, until = 100)}"

						onRecordHoldingLocation("$selectedHoldingArea $bin")
					}
				}
			)
		}
	}

	if (holdLocationState is GenericViewState.Success) {
		Handler(Looper.getMainLooper()).postDelayed({
			onStageCompletionCallBack.invoke()
		}, 1000)
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

	if (holdSlipState == GenericViewState.Loading) {
		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				color = BrandColor.GRAY_900
			)
		}
	} else if (holdSlipState == GenericViewState.Success && isStepActive(PrepStep.PACK)) {
		handlePrintHoldSlip()
	} else if (holdSlipState == GenericViewState.Failure) {
		// Is this still valid in the flow
		/*InfoModal(
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
		)*/
	}
}

@Composable
private fun PackOrderCard(
	isActive: Boolean,
	isComplete: Boolean,
	onPackOrder: () -> Unit = {}
) {
	ExpandableStepCard(
		stepNumber = "1",
		title = stringResource(id = R.string.pack_order),
		isActive = isActive,
		isComplete = isComplete,
		extraContent = {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Image(
					modifier = Modifier.padding(vertical = 8.dp),
					imageVector = ImageVector.vectorResource(R.drawable.pack_order),
					contentDescription = stringResource(id = R.string.pack_order)
				)
				Text(
					modifier = Modifier.padding(horizontal = 16.dp),
					text = stringResource(id = R.string.pack_info),
					style = TextStyle(
						fontSize = 12.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(400),
						color = BrandColor.BLACK,
						letterSpacing = 0.5.sp
					)
				)
				PrimaryButton(
					modifier = Modifier.padding(vertical = 12.dp),
					text = stringResource(id = R.string.print_hold_slip),
					buttonState = ButtonState.DEFAULT,
					onButtonClick = onPackOrder
				)
			}
		}
	)
}

@Composable
private fun ScanAndPackUnitsCard(
	isActive: Boolean,
	isComplete: Boolean,
	packItems: List<PackTaskItem>?,
	onPackItem: (String) -> Boolean = { false },
	pickedBy: String?,
	onPackOrder: () -> Unit = {}
) {
	LaunchedEffect(packItems) {
		if (packItems?.all { it.isScanned } == true) {
			onPackOrder()
		}
	}

	ExpandableStepCard(
		stepNumber = "1",
		title = stringResource(id = R.string.scan_pack),
		isActive = isActive,
		isComplete = isComplete,
		extraContent = {
			Column {
				// Not using LazyColumn. Scrolling is handled above for entire screen.
				packItems?.forEach { prepTaskItem ->
					PackTaskItemCard(
						modifier = Modifier
							.background(BrandColor.WHITE)
							.fillMaxWidth()
							.clickable {
								if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
									onPackItem(prepTaskItem.scannedBarcode.orEmpty())
								}
							},
						packTaskItem = prepTaskItem
					)
					Divider(
						modifier = Modifier.padding(top = 8.dp),
						thickness = 1.dp,
						color = BrandColor.GRAY_350
					)
				}
				Text(
					modifier = Modifier.padding(vertical = 8.dp),
					text = stringResource(id = R.string.pack_info),
					style = TextStyle(
						fontSize = 12.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(400),
						color = BrandColor.BLACK,
						letterSpacing = 0.5.sp
					)
				)
				if (!pickedBy.isNullOrEmpty()) {
					TextInfo(
						label = stringResource(id = R.string.picked_by),
						value = pickedBy
					)
				}
			}
		}
	)
}

@Composable
private fun ScanLocationCard(
	isActive: Boolean,
	holdLocationState: GenericViewState,
	onScanClick: () -> Unit = {}
) {
	ExpandableStepCard(
		stepNumber = "3",
		title = stringResource(id = R.string.scan_location),
		isActive = isActive,
		extraContent = {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Image(
					modifier = Modifier.padding(top = 8.dp),
					imageVector = ImageVector.vectorResource(R.drawable.scanning_bin),
					contentDescription = "Scanning Bin"
				)
				DetailedCallToAction(
					modifier = Modifier
						.align(Alignment.CenterHorizontally),
					detailedCallToActionMode = when (holdLocationState) {
						is GenericViewState.Loading -> DetailedCallToActionMode.Loading()
						is GenericViewState.Success -> {
							DetailedCallToActionMode.Done()
						}
						else -> {
							DetailedCallToActionMode.Scan(stringResource(id = R.string.scan_location))
						}
					},
					onClick = onScanClick
				)
			}
		}
	)
}

@PreviewPdt
@Composable
private fun BOPISPrepDetailScreenPreview() {
	BOPISPrepDetailScreen(
		scanManager = NoOpScanManager(),
		isOrderAssembled = false,
		prepOrder = PrepOrder(
			athleteName = "Athlete",
			orderNumber = "1010101010",
			pickedBy = "Picker",
			subFulfillmentType = "",
			packItems = listOf(PACK_TASK_ITEM, PACK_TASK_ITEM)
		),
		holdingAreas = emptyList(),
		ipPrefix = "",
		printer = Printer(printerName = "BOPIS", ipAddress = "", connectionStatus = false),
		onConnectPrinter = { _, _ -> },
		onResetPrinter = {},
		currentPrepStage = InfoViewModel.PrepStage.PrepOrder,
		holdLocationState = GenericViewState.Idle
	)
}

@Preview
@Composable
private fun PackOrderCardPreview() {
	PackOrderCard(
		isActive = true,
		isComplete = false,
		onPackOrder = {}
	)
}

@Preview
@Composable
private fun ScanAndPackUnitsCardPreview() {
	ScanAndPackUnitsCard(
		isActive = true,
		isComplete = false,
		packItems = listOf(PACK_TASK_ITEM, PACK_TASK_ITEM),
		pickedBy = "Joe Ducko",
		onPackOrder = {}
	)
}

@Preview
@Composable
private fun SelectHoldingAreaCardPreview() {
	SelectHoldingAreaCard(
		stepNumber = "2",
		isActive = true,
		isComplete = false,
		holdingAreas = listOf(),
		selectedHoldingArea = "",
		isReprintActive = true
	)
}

@Preview
@Composable
private fun ScanLocationCardPreview() {
	ScanLocationCard(
		isActive = true,
		holdLocationState = GenericViewState.Idle
	)
}

private val PACK_TASK_ITEM = PackTaskItem(
	id = 1,
	sku = "2345",
	primaryAttr = ProductAttribute(name = "Color", value = "Cyclamen"),
	secondaryAttr = ProductAttribute(name = "Size", value = "7.5"),
	tertiaryAttr = ProductAttribute(name = "Style", value = "12345"),
	qty = 1,
	packedQty = 2,
	declinedQty = 2,
	productName = "Hoka Women’s Clifton 9 Running Shoes",
	productImageUrls = listOf(
		"https://picsum.photos/1705",
		"https://picsum.photos/1726",
		"https://picsum.photos/1701"
	)
)
