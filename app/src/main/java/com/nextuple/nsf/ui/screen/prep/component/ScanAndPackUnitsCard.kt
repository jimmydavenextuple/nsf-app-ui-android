package com.nextuple.nsf.ui.screen.prep.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.service.RingBarcodeScanManager
import com.nextuple.nsf.ui.component.ExpandableStepCard
import com.nextuple.nsf.ui.component.PackDeclineModal
import com.nextuple.nsf.ui.screen.prep.packTaskItem
import com.nextuple.nsf.ui.theme.BrandColor
import kotlinx.coroutines.delay

@Composable
fun ScanAndPackUnitsCard(
	isActive: Boolean,
	stepNumber: String,
	isComplete: Boolean,
	packItems: List<PackTaskItem>?,
	onPackItem: (String) -> Boolean = { false },
	pickedBy: String?,
	onAllItemsScanned: () -> Unit = {},
	onStageCompletionCallBack: () -> Unit = {},
	onConfirmDecline: (declineReason: String, index: Int, item: PackTaskItem) -> Unit
) {
	var ctx = LocalContext.current
	var showDeclineModal by remember {
		mutableStateOf(false)
	}
	var indexAndItemToDecline: Pair<Int, PackTaskItem?> by remember {
		mutableStateOf(-1 to null)
	}

	val ringBarcodeScanManager = remember { RingBarcodeScanManager() }
	val focusRequester = remember { FocusRequester() }

	LaunchedEffect(packItems) {
		if (packItems?.all { it.isDeclined } == true) {
			delay(1000)
			onStageCompletionCallBack()
		} else if (packItems?.all { it.isScanned || it.isDeclined } == true) {
			onAllItemsScanned()
		}
		ringBarcodeScanManager.clearScan()
		focusRequester.requestFocus()
	}

	ringBarcodeScanManager.onBarcodeScanned = { barcode ->
		Log.i("ScanAndPackUnitsCard:Scanner", "Scanned: $barcode");
		if(!onPackItem(barcode)) {
			Toast.makeText(
				ctx,
				"UPC mismatch",
				Toast.LENGTH_SHORT
			).show()
		}
	}

	ExpandableStepCard(
		stepNumber = stepNumber,
		title = stringResource(id = R.string.scan_pack),
		isActive = isActive,
		isComplete = isComplete,
		extraContent = {
			Column(modifier = Modifier.wrapContentHeight()
				.focusRequester(focusRequester) // Attach focusRequester to the composable
				.focusable()
				.onKeyEvent {
					ringBarcodeScanManager.handleKeyEvent(it)
				}) {
				// Not using LazyColumn. Scrolling is handled above for entire screen.
				packItems?.forEachIndexed { i, prepTaskItem ->
					if (!prepTaskItem.isDeclined) {
						PackTaskItemCard(
							modifier = Modifier
								.background(BrandColor.WHITE)
								.fillMaxWidth()
								.clickable {
									if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
										onPackItem(prepTaskItem.scannedBarcode.orEmpty())
									}
								},
							packTaskItem = prepTaskItem,
							onDeclineClick = {
								indexAndItemToDecline = i to prepTaskItem
								showDeclineModal = true
							}
						)
						HorizontalDivider(
							modifier = Modifier,
							thickness = 1.dp,
							color = BrandColor.GRAY_350
						)
					}
				}
				Text(
					modifier = Modifier.padding(vertical = 8.dp),
					text = stringResource(id = R.string.pack_info),
					style = TextStyle(
						fontSize = 12.sp,
						fontWeight = FontWeight(400),
						color = BrandColor.BLACK,
						letterSpacing = 0.5.sp
					)
				)
				if (!pickedBy.isNullOrEmpty()) {
					com.nextuple.nsf.ui.common.TextInfo(
						label = stringResource(id = R.string.picked_by),
						value = pickedBy
					)
				}
			}
		}
	)

	if (showDeclineModal) {
		PackDeclineModal(
			declineReasonsList = listOf("Damaged", "Not Found"),
			onDismissRequest = { showDeclineModal = false },
			onConfirmDecline = { declineReason ->
				indexAndItemToDecline.let { (i, item) ->
					if (item != null) {
						onConfirmDecline(declineReason, i, item)
					}
				}
			}
		)
	}
}

@Preview
@Composable
private fun ScanAndPackUnitsCardPreview() {
	ScanAndPackUnitsCard(
		isActive = true,
		stepNumber = "1",
		isComplete = false,
		packItems = listOf(packTaskItem, packTaskItem, packTaskItem),
		pickedBy = "Joe Ducko",
		onAllItemsScanned = {},
		onConfirmDecline = { _, _, _ -> }
	)
}

private val PACK_TASK_ITEM = PackTaskItem(
	sku = "2345",
	primaryAttr = ProductAttribute(name = "Color", value = "Cyclamen"),
	secondaryAttr = ProductAttribute(name = "Size", value = "7.5"),
	tertiaryAttr = ProductAttribute(name = "Style", value = "12345"),
	qty = 1,
	productName = "Hoka Women’s Clifton 9 Running Shoes",
	productImageUrls = listOf(
		"https://picsum.photos/1705",
		"https://picsum.photos/1726",
		"https://picsum.photos/1701"
	)
)
