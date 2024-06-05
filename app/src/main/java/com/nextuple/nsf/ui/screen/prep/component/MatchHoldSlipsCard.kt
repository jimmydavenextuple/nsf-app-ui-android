package com.nextuple.nsf.ui.screen.prep.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.ui.common.HoldSlip
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.component.ExpandableStepCard
import com.nextuple.nsf.ui.screen.prep.BOPLPrepStep
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun MatchHoldSlipsCard(
	isPlaceActive: Boolean,
	isSelectActive: Boolean,
	isMultiUnit: Boolean,
	packItems: List<PackTaskItem>,
	athlete: String,
	onReprintHoldSlip: () -> Unit,
	onUpdateStep: () -> Unit,
	onToggleMultiUnitModal: () -> Unit
) {
	if (isMultiUnit) {
		PlaceMultiUnitsCard(
			isActive = isPlaceActive,
			isNextStepActive = isSelectActive,
			onReprintHoldSlip = { onReprintHoldSlip() },
			updateStep = { onUpdateStep() },
			packItems = packItems,
			athlete = athlete,
			toggleMultiUnitModal = { onToggleMultiUnitModal() }
		)
	} else {
		PlaceHoldCard(
			isActive = isPlaceActive,
			isNextStepActive = isSelectActive,
			onReprintHoldSlip = { onReprintHoldSlip() },
			updateStep = { onUpdateStep() }
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
				goToNextScreen = { updateStep(BOPLPrepStep.SELECT_HOLDING) }
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
				goToNextScreen = { updateStep(BOPLPrepStep.SELECT_HOLDING) },
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
			Image(
				modifier = Modifier.size(height = 188.dp, width = 168.dp),
				painter = painterResource(id = R.drawable.ic_bopl_treadmill),
				contentDescription = ""
			)
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
		PrimaryButton(
			modifier = Modifier.fillMaxWidth(.7f),
			text = "NEXT",
			enabled = packItems.filter { packTaskItem -> packTaskItem.isScanned }.size == packItems.size
		) {
			goToNextScreen()
		}
	}
}

@Preview
@Composable
private fun MatchSingularHoldSlipsCardPreview() {
	MatchHoldSlipsCard(
		isPlaceActive = true,
		isSelectActive = false,
		isMultiUnit = false,
		packItems = listOf(),
		athlete = "Joe Ducko",
		onReprintHoldSlip = { },
		onUpdateStep = { },
		onToggleMultiUnitModal = { }
	)
}

@Preview
@Composable
private fun MatchMultiUnitHoldSlipsCardPreview() {
	MatchHoldSlipsCard(
		isPlaceActive = true,
		isSelectActive = false,
		isMultiUnit = true,
		packItems = listOf(
			PackTaskItem(
				sku = "",
				qty = 1,
				productName = "",
				productImageUrls = listOf()
			),
			PackTaskItem(
				sku = "",
				qty = 1,
				productName = "",
				productImageUrls = listOf()
			)
		),
		athlete = "Joe Ducko",
		onReprintHoldSlip = { },
		onUpdateStep = { },
		onToggleMultiUnitModal = { }
	)
}
