package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.ui.common.HoldSlip
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.MultiOptionModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.component.ExpandableStepCard
import com.nextuple.nsf.ui.component.PrinterBOPLModal
import com.nextuple.nsf.ui.component.PrinterModal
import com.nextuple.nsf.ui.screen.prep.BOPLPrepStep.PLACE_HOLD_SLIP
import com.nextuple.nsf.ui.screen.prep.BOPLPrepStep.PRINT_HOLD_SLIP
import com.nextuple.nsf.ui.screen.prep.BOPLPrepStep.SELECT_HOLDING
import com.nextuple.nsf.ui.screen.prep.component.PrepDetailScaffold
import com.nextuple.nsf.ui.screen.prep.component.SelectHoldingAreaCard
import com.nextuple.nsf.ui.screen.settings.getPrinterConnectIcon
import com.nextuple.nsf.ui.screen.settings.getPrinterIcon
import com.nextuple.nsf.ui.state.PrepViewModel
import com.nextuple.nsf.ui.state.PrepViewModel.PrepOrder
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Pack
import com.nextuple.nsf.ui.state.PrepViewModel.Step.Stage
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
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
	prepOrder: PrepOrder,
	ipPrefix: String?,
	printer: Printer,
	printerList: List<Printer>?,
	onConnectPrinter: (Printer, String) -> Unit,
    currentPrepStage: PrepViewModel.Step,
	printerConnectionState: GenericViewState = GenericViewState.Idle,
	onResetPrinter: () -> Unit,
	holdSlipState: GenericViewState = GenericViewState.Idle,
    startPack: () -> Unit,
    completePackAndGetHoldSlip: () -> Unit,
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

	if (!showAcceptedScreen) {
		PrepDetailScaffold(
			athleteName = prepOrder.athleteName,
			orderNumber = prepOrder.orderNumber
		) {
			PrintHoldSlipCard(
				isActive = isPrintActive(),
				printer = printer,
				onPrintHoldSlipEvent = {
					startPack()
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

			if (isMultiUnit) {
				PlaceMultiUnitsCard(
					isActive = isPlaceActive(),
					isNextStepActive = isSelectActive(),
					onReprintHoldSlip = { handlePrintHoldSlip() },
					updateStep = {
						completePackAndGetHoldSlip()
						updateStep(SELECT_HOLDING)
					},
					packItems = prepOrder.packItems,
					athlete = prepOrder.athleteName,
					toggleMultiUnitModal = {
						isStepNextActive = false
						showMultiUnitModal = true
					}
				)
			} else {
				PlaceHoldCard(
					isActive = isPlaceActive(),
					isNextStepActive = isSelectActive(),
					onReprintHoldSlip = { handlePrintHoldSlip() },
					updateStep = {
						completePackAndGetHoldSlip()
						updateStep(SELECT_HOLDING)
					}
				)
			}

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
	if (holdSlipState == GenericViewState.Loading) {
		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				color = BrandColor.GRAY_900
			)
		}
	} else if (holdSlipState == GenericViewState.Success && isPlaceActive() && !isPrinted) {
		handlePrintHoldSlip()
		isPrinted = true
	} else if (holdSlipState == GenericViewState.Failure) {
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
}

@Composable
private fun PrintHoldSlipCard(
	isActive: Boolean,
	printer: Printer,
	packItems: List<PackTaskItem>,
	isMultiUnit: Boolean,
	onPrintHoldSlipEvent: () -> Unit,
	showPrinterModalEvent: () -> Unit,
	onDisConnectPrinter: (Printer) -> Unit
) {
	@Composable
	fun ExtraContent() {
		Text(
			modifier = Modifier
				.padding(start = 60.dp, top = 12.dp),
			text = "Printer".uppercase(),
			style = TextStyle(
				fontSize = 14.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight(700),
				letterSpacing = 1.5.sp
			)
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 10.dp, bottom = 4.dp),
			horizontalArrangement = Arrangement.Center
		) {
			Card(
				modifier = Modifier
					.width(207.dp)
					.wrapContentHeight(),
				shape = RoundedCornerShape(4.dp),
				colors = CardDefaults.cardColors(containerColor = if (printer.connectionStatus) BrandColor.GREEN_50 else BrandColor.GRAY_100)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Image(
						modifier = Modifier.weight(0.2f),
						painter = painterResource(
							id = getPrinterIcon(printer.connectionStatus)
						),
						contentDescription = "Printer Image"
					)
					Column(
						modifier = Modifier
							.weight(0.6f)
							.padding(start = 4.dp, end = 4.dp)
					) {
						if (printer.connectionStatus) {
							Text(
								text = printer.ipAddress,
								maxLines = 1,
								style = TextStyle(
									fontWeight = FontWeight.Normal,
									fontFamily = FontFamily.ARCHIVO,
									fontStyle = FontStyle.Normal,
									fontSize = 12.sp,
									color = BrandColor.BLACK,
									letterSpacing = 0.5.sp
								)
							)
						}
						Text(
							text = if (printer.connectionStatus) {
								stringResource(id = R.string.printer_connected)
							} else {
								"No Printer Selected"
							},
							maxLines = 1,
							style = TextStyle(
								fontWeight = FontWeight.Normal,
								fontFamily = FontFamily.ARCHIVO,
								fontStyle = FontStyle.Italic,
								fontSize = 12.sp,
								color = BrandColor.BLACK,
								letterSpacing = 0.5.sp
							)
						)
					}
					if (printer.connectionStatus) {
						Image(
							modifier = Modifier
								.padding(7.dp)
								.clickable {
									onDisConnectPrinter(printer)
								},
							painter = painterResource(id = getPrinterConnectIcon(printer.connectionStatus)),
							contentDescription = "connect printer"
						)
					}
				}
			}
		}

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.Center
		) {
			PrimaryButton(
				modifier = Modifier
					.fillMaxWidth(.65f)
					.padding(vertical = 15.dp),
				text = if (printer.connectionStatus && !isMultiUnit) stringResource(id = R.string.print_hold_slip) else if (printer.connectionStatus) stringResource(R.string.print_all) else "Select Printer".uppercase(),
				onButtonClick = {
					if (printer.connectionStatus) {
						onPrintHoldSlipEvent()
					} else {
						showPrinterModalEvent()
					}
				}
			)
		}
	}

	if (packItems.size <= 1) {
		ExpandableStepCard(
			stepNumber = "1",
			title = stringResource(R.string.bopl_prep_1),
			isActive = isActive,
			isComplete = !isActive,
			extraContent = { ExtraContent() }
		)
	} else {
		ExpandableStepCard(
			stepNumber = "1",
			title = null,
			annotatedTitle = if (isActive) {
				buildAnnotatedString {
					append("Print ")
					withStyle(style = SpanStyle(color = BrandColor.PINK_NT)) {
						append(packItems.size.toString())
					}
					append(" Hold Slips")
				}
			} else {
				buildAnnotatedString {
					append("Print ")
					append(packItems.size.toString())
					append(" Hold Slips")
				}
			},
			isActive = isActive,
			isComplete = !isActive,
			extraContent = { ExtraContent() }
		)
	}
}

@Composable
private fun PlaceHoldCard(
	isActive: Boolean,
	isNextStepActive: Boolean,
	onReprintHoldSlip: () -> Unit,
	updateStep: (BOPLPrepStep) -> Unit
) {
	ExpandableStepCard(
		stepNumber = "2",
		title = stringResource(R.string.bopl_prep_2),
		isActive = isActive,
		isComplete = isNextStepActive,
		extraContent = {
			BOPLExtra(
				onReprintHoldSlip = onReprintHoldSlip,
				goToNextScreen = { updateStep(SELECT_HOLDING) }
			)
		}
	)
}

@Composable
private fun PlaceMultiUnitsCard(
    isActive: Boolean,
    isNextStepActive: Boolean,
    onReprintHoldSlip: () -> Unit,
    updateStep: (BOPLPrepStep) -> Unit,
    athlete: String?,
    packItems: List<PackTaskItem>?,
    toggleMultiUnitModal: () -> Unit
) {
	ExpandableStepCard(
		stepNumber = "2",
		title = stringResource(R.string.match_slips_to_units),
		isActive = isActive,
		isComplete = isNextStepActive,
		extraContent = {
			BOPLMultiUnitExtra(
				onReprintHoldSlip = { onReprintHoldSlip() },
				goToNextScreen = { updateStep(SELECT_HOLDING) },
				athlete = athlete,
				packItems = packItems ?: emptyList(),
				toggleMultiUnitModal = { toggleMultiUnitModal() }
			)
		}
	)
}

@Composable
fun BOPLExtra(onReprintHoldSlip: () -> Unit, goToNextScreen: () -> Unit) {
	Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
		Row {
			Image(modifier = Modifier.size(height = 188.dp, width = 168.dp), painter = painterResource(id = R.drawable.ic_bopl_treadmill), contentDescription = "")
		}
		Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.Center) {
			ClickableText(
				text = AnnotatedString(stringResource(id = R.string.reprint_hold_slip)),
				style = TextStyle(
					fontSize = 12.sp,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight(700),
					color = BrandColor.BLACK,
					textAlign = TextAlign.Center,
					letterSpacing = 1.5.sp,
					textDecoration = TextDecoration.Underline
				),
				onClick = { onReprintHoldSlip() }
			)
		}
		PrimaryButton(modifier = Modifier.fillMaxWidth(.7f), text = "NEXT") {
			goToNextScreen()
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BOPLMultiUnitExtra(
	onReprintHoldSlip: () -> Unit,
	goToNextScreen: () -> Unit,
	athlete: String?,
	packItems: List<PackTaskItem>,
	toggleMultiUnitModal: () -> Unit
) {
	val split = athlete?.split(" ")
	val lastName = split?.get(split.lastIndex)
	var index = 0
	Column(
		modifier = Modifier
			.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(BrandColor.GRAY_100),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			FlowRow(
				modifier = Modifier
					.padding(vertical = 4.dp)
					.fillMaxWidth()
					.wrapContentHeight(),
				horizontalArrangement = Arrangement.Center,
				maxItemsInEachRow = 2
			) {
				packItems.forEach {
					Row(
						modifier = Modifier
							.padding(vertical = 4.dp, horizontal = 4.dp)
							.clickable {
								if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
									toggleMultiUnitModal()
								}
							}
					) {
						index++
						HoldSlip(
							athleteName = null,
							isSmallSize = true,
							lastName = lastName ?: "",
							packageNum = index.toString(),
							totalPackageNum = packItems.size.toString(),
							isScanable = true,
							isSelected = false,
							scanStatus = if (it.isScanned) {
								DetailedCallToActionMode.Done(
									contentColor = BrandColor.GREEN_500
								)
							} else {
								DetailedCallToActionMode.Scan(text = "")
							}
						)
					}
				}
			}
		}

		Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.Center) {
			ClickableText(
				text = AnnotatedString(stringResource(id = R.string.reprint_hold_slip)),
				style = TextStyle(
					fontSize = 12.sp,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight(700),
					color = BrandColor.BLACK,
					textAlign = TextAlign.Center,
					letterSpacing = 1.5.sp,
					textDecoration = TextDecoration.Underline
				),
				onClick = { onReprintHoldSlip() }
			)
		}
		PrimaryButton(modifier = Modifier.fillMaxWidth(.7f), text = "NEXT", enabled = packItems.filter { packTaskItem -> packTaskItem.isScanned }.size == packItems.size) {
			goToNextScreen()
		}
	}
}

@Composable
fun HoldingLocation(holdingArea: String) {
	Column(
		Modifier
			.background(BrandColor.BLUE_800_NT)
			.fillMaxHeight()
			.fillMaxWidth(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Image(modifier = Modifier.size(50.dp), painter = painterResource(id = R.drawable.ic_check_white), contentDescription = "Complete")
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
fun PreviewBOPLPackScreen() {
	BOPLPrepDetailsScreen(
		prepOrder = PrepOrder(
			athleteName = "Mary Kate",
			orderNumber = "12345678",
			pickedBy = "Picker",
			subFulfillmentType = "",
			packItems = listOf(packTaskItem, packTaskItem)
		),
		printer = Printer("BOPL", "123", connectionStatus = true),
		startPack = {},
		completePackAndGetHoldSlip = {},
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
		onPackItem = { _ -> false }
	)
}

@Composable
@PreviewPdt
fun PreviewHoldingLocationScreen() {
	HoldingLocation(holdingArea = "Main Holding Location")
}

