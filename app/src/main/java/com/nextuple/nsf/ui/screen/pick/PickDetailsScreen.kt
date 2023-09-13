package com.nextuple.nsf.ui.screen.pick

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PickTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.CallToAction
import com.nextuple.nsf.ui.common.CallToActionMode
import com.nextuple.nsf.ui.common.Carousel
import com.nextuple.nsf.ui.common.HorizontalProgressBar
import com.nextuple.nsf.ui.common.MultiOptionModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.ScrollToReveal
import com.nextuple.nsf.ui.common.TextInfo
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager

@Composable
fun PickDetailsScreen(
	scanManager: ScanManager,
	progressBarBackgroundColor: Color,
	pickDeclineState: GenericViewState = GenericViewState.Idle,
	recordPickState: GenericViewState = GenericViewState.Idle,
	declineCodesState: GenericViewState = GenericViewState.Idle,
	currentPickTaskItem: PickTaskItem?,
	unitsWorked: Int,
	totalUnits: Int,
	declineModalOptions: List<String>?,
	onDeclineReasonSelected: (declineReason: String) -> Unit = {},
	onDeclineClick: () -> Unit = {},
	onItemPick: (upc: String, symbology: String?) -> Unit,
	onRecordPickCompletion: () -> Unit = {},
	onDeclineCompletion: () -> Unit = {}
) {
	var showDeclineModal by remember {
		mutableStateOf(false)
	}

	LaunchedEffect(Unit) {
		scanManager.set(onItemPick)
	}

	if (pickDeclineState == GenericViewState.Success) {
		onDeclineCompletion.invoke()
	} else {
		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.background(color = BrandColor.GRAY_50),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				if (totalUnits > 0) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.background(progressBarBackgroundColor)
							.padding(start = 20.dp)
					) {
						HorizontalProgressBar(
							modifier = Modifier.padding(end = 129.dp),
							unitsCompleted = unitsWorked,
							totalUnits = totalUnits,
							title = stringResource(id = R.string.units_worked),
							textColor = BrandColor.GRAY_50
						)

						Spacer(
							modifier = Modifier
								.fillMaxWidth()
								.background(progressBarBackgroundColor)
								.height(8.dp)
						)
					}
				}
				ScrollToReveal(
					mainContent = {
						Column(
							modifier = Modifier
								.wrapContentHeight()
								.fillMaxWidth()
								.padding(top = 12.dp)
						) {
							Text(
								modifier = Modifier.padding(horizontal = 30.dp),
								text = currentPickTaskItem?.productBrand?.uppercase().orEmpty(),
								maxLines = 1,
								overflow = TextOverflow.Ellipsis,
								color = BrandColor.PINK_NT,
								fontFamily = FontFamily.ARCHIVO,
								fontSize = 15.sp,
								fontWeight = FontWeight.Bold,
								letterSpacing = 1.5.sp
							)
							Text(
								modifier = Modifier
									.padding(horizontal = 30.dp)
									.width(210.dp),
								text = currentPickTaskItem?.productName ?: "",
								maxLines = 2,
								overflow = TextOverflow.Ellipsis,
								fontFamily = FontFamily.ARCHIVO,
								fontSize = 15.sp,
								fontWeight = FontWeight.Normal,
								letterSpacing = 0.5.sp
							)
							Spacer(modifier = Modifier.height(12.dp))

							Carousel(
								modifier = Modifier
									.size(136.dp)
									.align(Alignment.CenterHorizontally),
								images = currentPickTaskItem?.productImageUrls ?: emptyList()
							)

							Spacer(modifier = Modifier.height(12.dp))

							ProductAttributes(currentItem = currentPickTaskItem)

							Spacer(modifier = Modifier.height(12.dp))

							CallToAction(
								modifier = Modifier
									.size(58.dp)
									.align(Alignment.CenterHorizontally),
								callToActionMode = when (recordPickState) {
									is GenericViewState.Loading -> {
										CallToActionMode.Loading()
									}

									is GenericViewState.Success -> {
										if (totalUnits != unitsWorked) {
											Handler(Looper.getMainLooper()).postDelayed({
												onRecordPickCompletion.invoke()
											}, 1500)
										} else {
											onRecordPickCompletion.invoke()
										}
										CallToActionMode.Done()
									}

									else -> {
										CallToActionMode.Scan()
									}
								},
								onClick = {
									if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
										onItemPick(
											currentPickTaskItem?.upcs?.firstOrNull().orEmpty(),
											"upc"
										)
									}
								}
							)
						}
					},
					secondaryContent = {
						PrimaryButton(
							modifier = Modifier
								.fillMaxWidth()
								.height(72.dp),
							buttonShape = RoundedCornerShape(0.dp),
							buttonColor = BrandColor.GRAY_800,
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
							text = stringResource(id = R.string.decline),
							textSize = 16.sp
						)
					},
					secondaryModifier = Modifier
						.fillMaxWidth()
						.padding(top = 17.dp),
					defaultRevealContent = false
				)
			}

			if (pickDeclineState == GenericViewState.Loading) {
				CircularProgressIndicator(
					color = BrandColor.GRAY_900
				)
			}
		}

		if (showDeclineModal) {
			if (declineModalOptions != null) {
				MultiOptionModal(
					title = stringResource(id = R.string.decline_reason),
					subTitle = stringResource(id = R.string.decline_reason_subtitle),
					buttons = declineModalOptions.map { it.uppercase() },
					buttonClick = { declineReason ->
						onDeclineReasonSelected(declineReason)
						showDeclineModal = false
					},
					crossIconClick = { showDeclineModal = false },
					onDismissRequest = { showDeclineModal = false }
				)
			}
		}
	}
}

@Composable
private fun ProductAttributes(currentItem: PickTaskItem?) {
	Box(
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			modifier = Modifier.align(Alignment.Center),
			horizontalArrangement = Arrangement.spacedBy(36.dp)
		) {
			Column(
				modifier = Modifier.width(92.dp),
				verticalArrangement = Arrangement.spacedBy(6.dp)
			) {
				TextInfo(
					label = stringResource(R.string.location),
					value = currentItem?.locations?.firstOrNull(),
					valueMaxLines = 1
				)

				TextInfo(
					label = currentItem?.primaryAttr?.name.orEmpty(),
					value = currentItem?.primaryAttr?.value,
					valueMaxLines = 1
				)
				TextInfo(
					label = stringResource(R.string.upc),
					value = currentItem?.upcs?.firstOrNull(),
					valueMaxLines = 1
				)
			}
			Column(
				verticalArrangement = Arrangement.spacedBy(6.dp)
			) {
				TextInfo(
					label = stringResource(R.string.on_hand),
					value = currentItem?.onHandQty?.toString()
				)
				TextInfo(
					label = currentItem?.secondaryAttr?.name.orEmpty(),
					value = currentItem?.secondaryAttr?.value
				)
				TextInfo(
					label = currentItem?.tertiaryAttr?.name.orEmpty(),
					value = currentItem?.tertiaryAttr?.value
				)
			}
		}
	}
}

@Composable
@PreviewPdt
fun PickDetailsScreenPreview() {
	PickDetailsScreen(
		scanManager = NoOpScanManager(),
		progressBarBackgroundColor = BrandColor.BLUE_800_NT,
		pickDeclineState = GenericViewState.Loading,
		currentPickTaskItem = PickTaskItem(
			sku = "2345",
			productBrand = "BOMBAS",
			productName = "Hoka Women’s Clifton 9 Running Shoes",
			productImageUrls = listOf(
				"https://picsum.photos/1705",
				"https://picsum.photos/1726",
				"https://picsum.photos/1701"
			),
			locations = listOf("F1.S1.04A"),
			primaryAttr = ProductAttribute(name = "Color", value = "Cyclamen"),
			secondaryAttr = ProductAttribute(name = "Size", value = "7.5"),
			tertiaryAttr = ProductAttribute(name = "Style", value = "12345"),
			onHandQty = 10,
			upcs = listOf("123456789101"),
			qty = 1,
			declinedQty = 0,
			pickedQty = 0
		),
		unitsWorked = 1,
		totalUnits = 3,
		declineModalOptions = listOf(),
		onItemPick = { _, _ -> }
	)
}
