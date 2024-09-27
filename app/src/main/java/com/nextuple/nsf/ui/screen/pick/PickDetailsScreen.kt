package com.nextuple.nsf.ui.screen.pick

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.R.string
import com.nextuple.nsf.retrofit.dto.PickTaskItem
import com.nextuple.nsf.service.BarcodeScanManager
import com.nextuple.nsf.ui.common.AttributeText
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.Carousel
import com.nextuple.nsf.ui.common.Tag
import com.nextuple.nsf.ui.common.HorizontalProgressBar
import com.nextuple.nsf.ui.common.ImageModal
import com.nextuple.nsf.ui.common.MultiOptionModal
import com.nextuple.nsf.ui.common.MultiOptionSubstitutionModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.ScrollToReveal
import com.nextuple.nsf.ui.common.SecondaryButton
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToAction
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.nav.Screen
import com.nextuple.nsf.ui.screen.barcodescanner.BarcodeScannerScreen
import com.nextuple.nsf.ui.state.BarcodeScannerViewModel
import com.nextuple.nsf.ui.state.PickViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdtNordOne
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.FulfillmentType
import com.nextuple.nsf.util.FulfillmentType.BOPIS
import com.nextuple.nsf.util.SubFulfillmentType

val DAMAGE_UI_REASON = "Damaged".uppercase()
val DAMAGE_REASON = "Damage".uppercase()
val OTHER_REASON = "Other".uppercase()

/**
 * @param declineModalOptions a linked map with display string as key and decline reason as value
 */
@Composable
fun PickDetailsScreen(
	barcodeScannerVM: BarcodeScannerViewModel,
	pickVM: PickViewModel,
	scanManager: ScanManager,
	pickDeclineState: GenericViewState = GenericViewState.Idle,
	recordPickState: GenericViewState = GenericViewState.Idle,
	declineCodesState: GenericViewState = GenericViewState.Idle,
	fulfillmentType: FulfillmentType?,
	subFulfillmentType: SubFulfillmentType?,
	currentPickTaskItem: PickTaskItem?,
	unitsWorked: Int,
	totalUnits: Int,
	declineModalOptions: LinkedHashMap<String, String>?,
	onDeclineReasonSelected: (declineReason: String, declineReasonText: String) -> Unit = { _, _ -> },
	onDeclineClick: () -> Unit = {},
	onCheckItemScan: (upc: String, symbology: String?) -> Boolean,
	onItemPick: (upc: String, pickLocation: String?) -> Unit,
	onRecordPickCompletion: () -> Unit = {},
	onDeclineCompletion: () -> Unit = {},
) {
	LaunchedEffect(Unit) {
		scanManager.set(onItemPick)
	}

	val scannedResult = barcodeScannerVM.scannedBarcode

	var showToast by remember {
		mutableStateOf(false);
	}

	var showDeclineModal by remember {
		mutableStateOf(false)
	}
	var showDetailDeclineModal by remember {
		mutableStateOf(false)
	}
	var showImageModal by remember {
		mutableStateOf(false)
	}
	var showPickLocationModal by remember {
		mutableStateOf(false)
	}
	var showBarcodeScanner by remember {
		mutableStateOf(false)
	}
	val showSubstitutionModal by pickVM.showSubstitutionModal.observeAsState()

	val clearanceColor by remember {
		mutableStateOf(currentPickTaskItem?.clearanceColorRgb)
	}
	var hideDecline by remember {
		mutableStateOf(false)
	}
	var lastScannedUpc by remember {
		mutableStateOf<String?>(null)
	}
	var lastScannedSymbology by remember {
		mutableStateOf<String?>(null)
	}

	var currentIndex by remember {
		mutableIntStateOf(0)
	}

	val defaultLocations = when (fulfillmentType) {
		BOPIS -> {
			when (subFulfillmentType) {
				SubFulfillmentType.BOPIS -> listOf("Sales Floor")
				SubFulfillmentType.BOPL -> listOf("BOPL Area")
				else -> emptyList()
			}
		}

		else -> emptyList()
	}
	val locations = currentPickTaskItem?.locations.orEmpty()
	val allLocations = locations.plus(defaultLocations)
	val remainingPickQty = currentPickTaskItem?.getRemainingPickQty()


	scannedResult?.let {
		lastScannedUpc = null
		lastScannedSymbology = null
		Log.i("TESTPOP", "scannedResult")
		if (onCheckItemScan(it, "upc")) {
			lastScannedUpc = it
			lastScannedSymbology = "upc"

			if (allLocations.size > 1) {
				showPickLocationModal = true
			} else {
				onItemPick(it, allLocations.firstOrNull())
			}
		}
		showBarcodeScanner = false
		barcodeScannerVM.reset()
	}

	fun onItemScan(upc: String, symbology: String?) {
		lastScannedUpc = null
		lastScannedSymbology = null

		if (onCheckItemScan(upc, symbology)) {
			lastScannedUpc = upc
			lastScannedSymbology = symbology

			if (allLocations.size > 1) {
				showPickLocationModal = true
			} else {
				onItemPick(upc, allLocations.firstOrNull())
			}
		} else {
			showToast = true;
		}
	}

	val barcodeScanManager = remember { BarcodeScanManager() }
	val focusRequester = remember { FocusRequester() }

	barcodeScanManager.onBarcodeScanned = { barcode ->
		lastScannedUpc = null
		lastScannedSymbology = null

		if (onCheckItemScan(barcode, "upc")) {
			lastScannedUpc = barcode
			lastScannedSymbology = "upc"

			if (allLocations.size > 1) {
				showPickLocationModal = true
			} else {
				onItemPick(barcode, allLocations.firstOrNull())
			}
		} else {
			showToast = true;
		}
	}

	LaunchedEffect(Unit) {
		scanManager.set(::onItemScan)
		barcodeScanManager.clearScan()
		focusRequester.requestFocus()
	}

	Box(
		modifier = Modifier
			.fillMaxHeight()
			.fillMaxWidth()
			.focusRequester(focusRequester) // Attach focusRequester to the composable
			.focusable()
			.onKeyEvent {
				barcodeScanManager.handleKeyEvent(it)
			},
		contentAlignment = Alignment.Center
	) {
		Column(
			modifier = Modifier
				.fillMaxHeight()
				.fillMaxWidth()
				.background(color = BrandColor.GRAY_50),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			if (totalUnits > 0) {
				HorizontalProgressBar(
					modifier = Modifier
						.background(BrandColor.GRAY_100)
						.height(33.dp)
						.padding(start = 20.dp, end = 40.dp, top = 8.dp, bottom = 8.dp),
					title = stringResource(id = string.units),
					workedCount = unitsWorked,
					totalCount = totalUnits
				)
				Spacer(
					modifier = Modifier
						.fillMaxWidth()
						.background(BrandColor.GRAY_300)
						.height(1.25.dp)
				)
			}

			val scrollState = rememberScrollState()
			LaunchedEffect(
				key1 = hideDecline
			) {
				if (hideDecline) {
					scrollState.scrollTo(0)
					hideDecline = false
				}
			}
			ScrollToReveal(
				modifier = Modifier
					.fillMaxHeight()
					.fillMaxWidth(),
				scrollState = scrollState,
				mainContent = {
					Column(
						modifier = Modifier
							.fillMaxHeight()
							.fillMaxWidth()
							.padding(top = 6.dp, start = 30.dp, end = 30.dp, bottom = 6.dp)
					) {
						Row(
							modifier = Modifier
								.fillMaxHeight()
								.fillMaxWidth()
								.padding(end = 0.dp),
							horizontalArrangement = Arrangement.SpaceBetween
						) {

							Text(
								text = currentPickTaskItem?.productBrand?.uppercase().orEmpty(),
								maxLines = 1,
								overflow = TextOverflow.Ellipsis,
								color = BrandColor.BLACK,
								fontSize = 14.sp,
								fontWeight = FontWeight(700),
								letterSpacing = 1.5.sp
							)
							Text(
								text = subFulfillmentType.toString(),
								maxLines = 1,
								color = BrandColor.BLACK,
								fontSize = 14.sp,
								fontWeight = FontWeight(700),
								letterSpacing = 1.5.sp
							)
						}

						Text(
							modifier = Modifier
								.width(210.dp),
							text = currentPickTaskItem?.productName ?: "",
							maxLines = if (clearanceColor.isNullOrEmpty() || (currentPickTaskItem?.substitutionAllowed == false && currentPickTaskItem.substitutions.isNullOrEmpty())) 2 else 1,
							overflow = TextOverflow.Ellipsis,
							fontSize = 12.sp,
							fontWeight = FontWeight.Normal,
							letterSpacing = 0.5.sp,
							lineHeight = 15.6.sp
						)

						Spacer(modifier = Modifier.height(6.dp))

						Row() {
							if (currentPickTaskItem?.substitutionAllowed == true && !currentPickTaskItem.substitutions.isNullOrEmpty()) {
								Tag(
									Modifier
										.padding(top = 0.dp, start = 0.dp, end = 5.dp, bottom = 0.dp),
									"115,147,179",
									"BLUE", "Substitutions Available")
							}
							if (!clearanceColor.isNullOrEmpty() && !currentPickTaskItem?.clearanceColorDesc.isNullOrEmpty()) {
								Tag(
									Modifier,
									clearanceColor.toString(),
									colorDesc = if (recordPickState == GenericViewState.Idle) {
										currentPickTaskItem?.clearanceColorDesc
											?: ""
									} else {
										""
									}
								)
							}
						}

						Spacer(modifier = Modifier.height(6.dp))

						if (recordPickState == GenericViewState.Idle && !showDeclineModal && !showImageModal) {
							PickImages(
								showImageModalToggle = { showImageModal = true },
								currentPickTaskItem = currentPickTaskItem,
								changeIndex = { currentIndex = it },
								currentIndex = currentIndex
							)
						} else {
							PickImages(
								showImageModalToggle = { showImageModal = true },
								currentPickTaskItem = null,
								changeIndex = { currentIndex = it },
								currentIndex = 0
							)
						}

						Spacer(modifier = Modifier.height(6.dp))

						ProductAttributes(
							upcs = currentPickTaskItem?.upcs.orEmpty(),
							styleNum = currentPickTaskItem?.style,
							locations = locations.ifEmpty { defaultLocations },
							onHandQty = currentPickTaskItem?.onHandQty,
							additionalAttributes = currentPickTaskItem?.additionalAttributes,
							lastReceived = currentPickTaskItem?.lastReceived.toString(),
							lastReturn = currentPickTaskItem?.lastReturn.toString()
						)

						Spacer(modifier = Modifier.height(20.dp))

						Row(
							modifier = Modifier
								.padding(top = 0.dp)
								.fillMaxHeight()
								.fillMaxWidth(),
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.Top,
						) {
							Column(
								modifier = Modifier.fillMaxWidth(),
								horizontalAlignment = Alignment.CenterHorizontally,
							) {
								DetailedCallToAction(
									detailedCallToActionMode = when {
										recordPickState is GenericViewState.Loading -> {
											DetailedCallToActionMode.Loading()
										}

										recordPickState is GenericViewState.Success -> {
											if (totalUnits != unitsWorked) {
												Handler(Looper.getMainLooper()).postDelayed({
													onRecordPickCompletion.invoke()
												}, 1500)
											} else {
												onRecordPickCompletion.invoke()
											}
											DetailedCallToActionMode.Done()
										}

										pickDeclineState == GenericViewState.Success -> {
											hideDecline = true

											if (totalUnits != unitsWorked) {
												Handler(Looper.getMainLooper()).postDelayed({
													onDeclineCompletion.invoke()
												}, 1500)
											} else {
												onDeclineCompletion.invoke()
											}
											DetailedCallToActionMode.Decline()
										}

										else -> {
											val scanText =
												if (remainingPickQty != null && remainingPickQty > 0) {
													"Pick $remainingPickQty"
												} else {
													"Pick"
												}

											DetailedCallToActionMode.Scan(scanText)
										}
									},
									onClick = {
										if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
											onItemScan(
												upc = currentPickTaskItem?.upcs?.firstOrNull()
													.orEmpty(),
												symbology = "upc"
											)
										}
									},
								)
							}
						}

						Spacer(modifier = Modifier.height(6.dp))

						Row(
							modifier = Modifier
								.padding(top = 0.dp)
								.fillMaxHeight()
								.fillMaxWidth(),
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.Bottom,
						) {
							Column(
								modifier = Modifier.fillMaxWidth(),
								horizontalAlignment = Alignment.CenterHorizontally,
							) {
								val scanText =
									if (remainingPickQty != null && remainingPickQty > 0) {
										"Pick $remainingPickQty"
									} else {
										"Pick"
									}
								DetailedCallToAction(
									detailedCallToActionMode = DetailedCallToActionMode.Camera(scanText),
									onClick = {
										showBarcodeScanner = true
									},
								)
							}
						}

					}
				},
				secondaryContent = {
					SecondaryButton(
						modifier = Modifier
							.fillMaxWidth(.83f)
							.height(64.dp),
						buttonShape = RoundedCornerShape(0.dp),
						buttonState =
						if (declineCodesState == GenericViewState.Loading) {
							ButtonState.LOADING
						} else {
							ButtonState.DEFAULT
						},
						onButtonClick = {
							onDeclineClick()
							showDeclineModal = true
						},
						text = stringResource(id = string.decline),
						textSize = 16.sp
					)
				},
				secondaryModifier = Modifier
					.fillMaxWidth()
					.padding(top = 30.dp, bottom = 10.dp),
				defaultRevealContent = false
			)
		}

		if (pickDeclineState == GenericViewState.Loading) {
			CircularProgressIndicator(
				color = BrandColor.GRAY_900
			)
		}
	}

	if (showDeclineModal && declineModalOptions != null) {
		if (subFulfillmentType != SubFulfillmentType.BOPL) {
			MultiOptionModal(
				title = stringResource(id = string.decline_reason),
				subTitle = stringResource(id = string.decline_reason_subtitle),
				buttons = declineModalOptions.keys.map { it.uppercase() },
				buttonClick = { displayStr ->
					val declineReason = declineModalOptions[displayStr] ?: return@MultiOptionModal
					onDeclineReasonSelected(declineReason, "")
					showDeclineModal = false
				},
				crossIconClick = { showDeclineModal = false },
				onDismissRequest = { showDeclineModal = false }
			)
		} else {
			MultiOptionModal(
				title = "Decline Reason",
				subTitle = null,
				buttons = listOf(DAMAGE_UI_REASON, OTHER_REASON),
				buttonClick = {
					if (it == DAMAGE_UI_REASON) {
						onDeclineReasonSelected(DAMAGE_REASON, "")
						showDeclineModal = false
					} else {
						showDeclineModal = false
						showDetailDeclineModal = true
					}
				},
				crossIconClick = { showDeclineModal = false },
				onDismissRequest = { showDeclineModal = false }
			)
		}
	}

	if (showPickLocationModal && allLocations.size > 1) {
		MultiOptionModal(
			title = stringResource(string.unit_locations),
			subTitle = stringResource(string.unit_locations_subtitle),
			buttons = allLocations.map { it.uppercase() },
			buttonClick = { location ->
				// Ensure the scan has already been verified prior to launching this modal.
				onItemPick(lastScannedUpc.orEmpty(), location)
				showPickLocationModal = false
			},
			crossIconClick = { showPickLocationModal = false },
			onDismissRequest = { showPickLocationModal = false }
		)
	}

	if(showBarcodeScanner) {
		BarcodeScannerScreen(
			barcodeScannerVM,
			currentPickTaskItem?.upcs?.firstOrNull().orEmpty()) {
			showBarcodeScanner = false;
		}
	}

	if (showImageModal && currentPickTaskItem != null) {
		ImageModal(
			productName = currentPickTaskItem.productName,
			brandName = currentPickTaskItem.productBrand,
			imageUrls = currentPickTaskItem.productHighResImageUrls,
			onDismissRequest = { showImageModal = false },
			startIndex = currentIndex,
			changeCurrentIndex = { currentIndex = it }
		)
	}

	if (showDetailDeclineModal && subFulfillmentType == SubFulfillmentType.BOPL) {
		DeclineDetailsDialog(
			onDismissRequest = {
				showDetailDeclineModal = false
				showDeclineModal = true
			},
			onDeclineReasonSelected = { declineReason, declineReasonText ->
				showDeclineModal = false
				onDeclineReasonSelected(declineReason, declineReasonText)
			}
		)
	}

	if (showSubstitutionModal == true) {
		MultiOptionSubstitutionModal(
			title = "Substitutions Available",
			subTitle = "The customer has approved substitutions. Please select one to pick it.",
			buttons = currentPickTaskItem?.substitutions!!,
			buttonClick = { displayStr ->
				pickVM.onSubstitutionSelected(displayStr)
				hideDecline = true
				pickVM.toggleShowSubstitutionModal()
			},
			crossIconClick = { pickVM.toggleShowSubstitutionModal() },
			onDismissRequest = { pickVM.toggleShowSubstitutionModal() }
		)
	}

	if(showToast) {
		Toast.makeText(LocalContext.current, "UPC mismatch", Toast.LENGTH_SHORT).show();
		showToast = false
	}
}

@Composable
private fun PickImages(
	showImageModalToggle: () -> Unit,
	currentPickTaskItem: PickTaskItem?,
	changeIndex: (Int) -> Unit,
	currentIndex: Int
) {
	Column(modifier = Modifier.fillMaxWidth()) {
		Carousel(
			modifier = Modifier
				.clickable { showImageModalToggle() }
				.align(Alignment.CenterHorizontally),
			images = currentPickTaskItem?.productImageUrls ?: emptyList(),
			size = DpSize(height = 116.dp, width = 116.dp),
			indexStart = currentIndex,
			changeIndex = { changeIndex(it) }
		)
	}
}

@Composable
private fun ProductAttributes(
	upcs: List<String>,
	styleNum: String?,
	locations: List<String>,
	onHandQty: Int?,
	additionalAttributes: Map<String, String>?,
	lastReturn: String,
	lastReceived: String
) {
	val clipboardManager: ClipboardManager = LocalClipboardManager.current
	val interactionSource = remember { MutableInteractionSource() }
	val interactionSourceOnHand = remember { MutableInteractionSource() }
	var showLocationDialog by remember { mutableStateOf(false) }
	var showAdditionalDetailsDialog by remember { mutableStateOf(false) }
	var copyCoordinatesOrigin: LayoutCoordinates? = null
	var copyCoordinatesX by remember { mutableIntStateOf(0) }
	var copyCoordinatesY by remember { mutableIntStateOf(0) }
	var isCopyPressed by remember { mutableStateOf(false) }

	val maxLocations = 4
	if (locations.size > 1) {
		val toIndex = Integer.min(locations.size, maxLocations)

		if (showLocationDialog) {
			LocationDialog(locations = locations.subList(0, toIndex)) {
				showLocationDialog = false
			}
		}
	}

	if (showAdditionalDetailsDialog) {
		AdditionalDetailsDialog(onHandQty.toString(), lastReceived, lastReturn) {
			showAdditionalDetailsDialog = false
		}
	}
	if (isCopyPressed) {
		CustomTextPopup(
			content = "Copied",
			onDismiss = { },
			onHide = { isCopyPressed = false },
			screenPositionX = copyCoordinatesX,
			screenPositionY = copyCoordinatesY
		)
	}

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 10.dp),
		contentAlignment = Alignment.Center
	) {
		Column {
			AttributeText(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 2.dp)
					.clickable(
						interactionSource = interactionSource,
						indication = LocalIndication.current
					) { showLocationDialog = true },
				iconImageVector = if (locations.size > 1) {
					ImageVector.vectorResource(
						R.drawable.ic_location
					)
				} else {
					null
				},
				label = stringResource(string.item_location),
				value = locations.firstOrNull(),
				valueMaxLines = 1
			)
			AttributeText(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 2.dp)
					.clickable(
						interactionSource = interactionSourceOnHand,
						indication = LocalIndication.current
					) { showAdditionalDetailsDialog = true }
					.background(color = BrandColor.GRAY_100),
				label = stringResource(id = string.on_hand),
				iconImageVector = ImageVector.vectorResource(R.drawable.ic_on_hand_icon),
				value = onHandQty?.toString()
			)
			AttributeText(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 2.dp)
					.pointerInput(upcs) {
						copyCoordinatesOrigin?.let {
							detectTapGestures { offset ->
								copyCoordinatesX = it.positionInRoot().x.toInt() + offset.x.toInt()
								copyCoordinatesY = it.positionInRoot().y.toInt() + offset.y.toInt()
								clipboardManager.setText(AnnotatedString(upcs.first()))
								isCopyPressed = true
							}
						}
					}
					.onGloballyPositioned {
						copyCoordinatesOrigin = it
					},
				label = stringResource(string.upc),
				iconImageVector = ImageVector.vectorResource(R.drawable.ic_copy_icon),
				value = upcs.firstOrNull(),
				valueMaxLines = 1
			)
			if (!styleNum.isNullOrEmpty()) {
				AttributeText(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 2.dp)
						.background(color = BrandColor.GRAY_100),
					label = "Style",
					value = styleNum,
					valueMaxLines = 1
				)
			}
			additionalAttributes?.forEach { (key, value) ->
				AttributeText(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 2.dp)
						.background(Color.Transparent),
					label = key,
					value = value,
					valueMaxLines = 1
				)
			}
		}
	}
}

@Composable
fun DeclineDetailsDialog(
	onDismissRequest: () -> Unit = {},
	onDeclineReasonSelected: (declineReason: String, declineReasonText: String) -> Unit = { _, _ -> }
) {
	var enabled = false
	var reason by remember { mutableStateOf(TextFieldValue("")) }
	Dialog(onDismissRequest = { onDismissRequest() }) {
		Surface(
			shape = RoundedCornerShape(4.dp),
			color = Color.White
		) {
			Column(
				modifier = Modifier
					.padding(20.dp)
					.fillMaxWidth()
			) {
				Row(
					Modifier
						.padding(bottom = 5.dp)
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = "Other Decline Reason",
						style = TextStyle(
							fontSize = 20.sp,
							fontWeight = FontWeight.Bold,
							letterSpacing = 0.5.sp
						)
					)
					Icon(
						modifier = Modifier
							.align(Alignment.CenterVertically)
							.clickable { onDismissRequest() },
						imageVector = ImageVector.vectorResource(R.drawable.ic_close),
						tint = BrandColor.GRAY_900,
						contentDescription = "close"
					)
				}
				Row {
					OutlinedTextField(
						label = { Text("Decline Reason") },
						colors = OutlinedTextFieldDefaults.colors(
							focusedContainerColor = Color.White,
							focusedBorderColor = Color.Black,
							focusedTextColor = Color.Black,
							focusedLabelColor = Color.Black
						),
						value = reason,
						onValueChange =
						{
							enabled = true
							reason = it
						}
					)
				}
				Row(horizontalArrangement = Arrangement.Center) {
					PrimaryButton(
						modifier = Modifier
							.fillMaxWidth()
							.padding(top = 10.dp),
						text = "Submit".uppercase(),
						enabled = enabled,
						onButtonClick = {
							onDismissRequest()
							onDeclineReasonSelected(DAMAGE_REASON, reason.text)
						}
					)
				}
			}
		}
	}
}

@Composable
@Preview
private fun DeclineDialogDialogPreview() {
	DeclineDetailsDialog()
}

@Composable
@PreviewPdtNordOne
fun PickDetailsScreenPreview() {
	PickDetailsScreen(
		barcodeScannerVM = BarcodeScannerViewModel(),
		pickVM = PickViewModel(null, null, null),
		scanManager = NoOpScanManager(),
		pickDeclineState = GenericViewState.Loading,
		fulfillmentType = BOPIS,
		subFulfillmentType = SubFulfillmentType.BOPIS,
		currentPickTaskItem = PickTaskItem(
			sku = "2345",
			productBrand = "BOMBAS",
			productName = "Hoka Women’s Clifton 9 Running Shoes",
			productImageUrls = listOf(
				"https://picsum.photos/1705",
				"https://picsum.photos/1726",
				"https://picsum.photos/1701"
			),
			productHighResImageUrls = listOf(
				"https://picsum.photos/1705",
				"https://picsum.photos/1726",
				"https://picsum.photos/1701"
			),
			locations = listOf("F1.S1.04A"),
			additionalAttributes = mapOf("Color" to "Cyclamen", "Size" to "7.5"),
			onHandQty = 10,
			upcs = listOf("4002560185162"),
			qty = 1,
			declinedQty = 0,
			pickedQty = 0,
			clearanceColorRgb = "0,175,65",
			clearanceColorDesc = "GREEN",
			style = "12345",
			substitutionAllowed = true,
			substitutions = listOf(
				PickTaskItem(
					sku = "2345",
					productBrand = "BOMBAS",
					productName = "Hoka Women’s Clifton 9 Running Shoes",
					productImageUrls = listOf(
						"https://picsum.photos/1705",
						"https://picsum.photos/1726",
						"https://picsum.photos/1701"
					),
					productHighResImageUrls = listOf(
						"https://picsum.photos/1705",
						"https://picsum.photos/1726",
						"https://picsum.photos/1701"
					),
					locations = listOf("F1.S1.04A"),
					additionalAttributes = mapOf("Color" to "Cyclamen", "Size" to "7.5"),
					onHandQty = 10,
					upcs = listOf("4002560185162"),
					qty = 1,
					declinedQty = 0,
					pickedQty = 0,
					clearanceColorRgb = "0,175,65",
					clearanceColorDesc = "GREEN",
					style = "12345",
				)
			)
		),
		unitsWorked = 1,
		totalUnits = 3,
		declineModalOptions = linkedMapOf(),
		onItemPick = { _, _ -> },
		onCheckItemScan = { _, _ -> true }
	)
}
