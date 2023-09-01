package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.ui.common.HorizontalProgressBar
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt

@Composable
fun PackDetailsScreen(
	progressBarBackgroundColor: Color,
	holdSlipState: GenericViewState = GenericViewState.Idle,
	athlete: String?,
	orderNum: String?,
	packItems: List<PackTaskItem>?,
	onPrintHoldSlip: () -> Unit,
	onPrintHoldSlipSuccessCallBack: () -> Unit,
	resetScreen: () -> Unit = {}
) {
	val unitsCompleted by remember { mutableStateOf(0) }
	val totalUnits by remember { mutableStateOf(3) }

	Column {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(progressBarBackgroundColor)
				.padding(start = 20.dp)
		) {
			HorizontalProgressBar(
				modifier = Modifier.padding(end = 129.dp),
				textColor = BrandColor.GRAY_50,
				unitsCompleted = unitsCompleted,
				totalUnits = totalUnits,
				title = stringResource(id = R.string.step_1_pack),
				isCustomTitle = true
			)

			Spacer(modifier = Modifier.height(12.dp))
		}

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(BrandColor.GRAY_200)
		) {
			HeaderText(
				modifier = Modifier
					.padding(top = 10.dp, start = 30.dp, bottom = 3.dp)
					.fillMaxWidth(.6f),
				title = stringResource(id = R.string.athlete),
				value = athlete ?: ""
			)
			Spacer(modifier = Modifier.weight(1f))
			HeaderText(
				modifier = Modifier.padding(end = 30.dp, top = 10.dp, bottom = 10.dp),
				title = stringResource(id = R.string.order_num),
				value = orderNum ?: ""
			)
		}

		Text(
			modifier = Modifier.padding(top = 4.dp, start = 24.dp, bottom = 11.dp),
			text = stringResource(id = R.string.pack_these_units),
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Bold,
			fontSize = 20.sp,
			letterSpacing = 0.5.sp
		)

		LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
			packItems?.let { packItems ->
				items(packItems) { prepTaskItem ->
					PackTaskItemCard(
						modifier = Modifier
							.padding(horizontal = 25.dp, vertical = 4.dp)
							.background(BrandColor.GRAY_100)
							.fillMaxWidth(),
						packTaskItem = prepTaskItem
					)
				}
			}
			item {
				PrimaryButton(
					modifier = Modifier
						.fillMaxWidth(0.6f)
						.align(Alignment.CenterHorizontally)
						.padding(vertical = 20.dp),
					text = stringResource(id = R.string.print_hold_slip),
					onButtonClick = { onPrintHoldSlip() }
				)
			}
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
	} else if (holdSlipState == GenericViewState.Success) {
		onPrintHoldSlipSuccessCallBack.invoke()
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
fun HeaderText(modifier: Modifier = Modifier, title: String, value: String) {
	Column(modifier = modifier) {
		Text(
			text = title,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Bold,
			fontSize = 10.sp,
			letterSpacing = 1.5.sp
		)
		Text(
			text = value,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Normal,
			fontSize = 16.sp,
			letterSpacing = 0.5.sp
		)
	}
}

@Composable
fun PackTaskItemCard(modifier: Modifier = Modifier, packTaskItem: PackTaskItem) {
	Row(
		modifier = modifier
	) {
		AsyncImage(
			model = packTaskItem.productImageUrls.firstOrNull(),
			error = painterResource(id = R.drawable.placeholder_image),
			placeholder = painterResource(id = R.drawable.placeholder_image),
			contentDescription = null,
			modifier = Modifier
				.padding(start = 15.dp, top = 9.dp, end = 23.dp, bottom = 15.dp)
				.size(64.dp)
		)

		Column(modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)) {
			Text(
				text = packTaskItem.productName,
				fontSize = 14.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight.Bold,
				letterSpacing = 0.5.sp
			)

			packTaskItem.primaryAttr?.value?.let {
				TextInfo(
					label = packTaskItem.primaryAttr.name,
					value = it
				)
			}
			packTaskItem.secondaryAttr?.value?.let {
				TextInfo(
					label = packTaskItem.secondaryAttr.name,
					value = it
				)
			}
			TextInfo(
				label = stringResource(id = R.string.upc),
				value = packTaskItem.scannedBarcode ?: ""
			)
		}
	}
}

@Composable
private fun TextInfo(label: String, value: String) {
	Text(
		text = "$label: $value",
		fontSize = 12.sp,
		fontFamily = FontFamily.ARCHIVO,
		fontWeight = FontWeight.Normal,
		letterSpacing = 0.5.sp
	)
}

@PreviewPdt
@Composable
fun PackTaskScreenPreview() {
	PackDetailsScreen(
		progressBarBackgroundColor = BrandColor.GREEN_900,
		holdSlipState = GenericViewState.Success,
		athlete = "Heisey, A",
		orderNum = "00000000000001",
		packItems = listOf(
			PackTaskItem(
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
			),
			PackTaskItem(
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
		),
		onPrintHoldSlip = {},
		onPrintHoldSlipSuccessCallBack = {}
	)
}

@Preview(showBackground = true)
@Composable
fun HeaderTextPreview() {
	HeaderText(title = "TITLE", value = "value")
}

@Preview(showBackground = true)
@Composable
fun PackTaskItemCardPreview() {
	PackTaskItemCard(
		packTaskItem = PackTaskItem(
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
	)
}

@Preview(showBackground = true)
@Composable
fun TextInfoRowPreview() {
	TextInfo(label = "Title", value = "value")
}
