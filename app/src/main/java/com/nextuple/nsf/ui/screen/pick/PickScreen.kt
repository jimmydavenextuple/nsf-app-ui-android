package com.nextuple.nsf.ui.screen.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.CircularProgressBar
import com.nextuple.nsf.ui.common.DisplayLegends
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.Legend
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt

@Composable
fun PickScreen(
	tasksUnassigned: Int? = null,
	unitsWorked: Int? = null,
	totalUnits: Int? = null,
	inProgressUnits: Int? = null,
	hasActiveTask: Boolean = false,
	onHasActiveTask: () -> Unit,
	storeOverviewState: GenericViewState = GenericViewState.Loading,
	startTaskStatus: GenericViewState = GenericViewState.Idle,
	onStartPicking: () -> Unit = {},
	startTaskCompletion: () -> Unit = {},
	resetScreen: () -> Unit = {}
) {
	var showInfoModal by remember { mutableStateOf(false) }

	fun dialogVisibility(visibility: Boolean) {
		showInfoModal = visibility
	}

	if (hasActiveTask) {
		LaunchedEffect(Unit) { onHasActiveTask() }
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = BrandColor.GRAY_50)
	) {
		if (
			storeOverviewState == GenericViewState.Loading ||
			startTaskStatus == GenericViewState.Loading
		) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center),
				color = BrandColor.GRAY_900
			)
		} else if (
			storeOverviewState == GenericViewState.Success &&
			unitsWorked != null &&
			totalUnits != null
		) {
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				CircularProgressBar(
					completedUnits = unitsWorked,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits ?: 0,
					centerText = "TOTAL UNITS WORKED",
					centerIconModifier = Modifier.clickable { dialogVisibility(true) }
				)

				Spacer(modifier = Modifier.height(20.dp))

				PrimaryButton(
					text = "START PICKING",
					enabled = tasksUnassigned != 0,
					onButtonClick = onStartPicking
				)

				if (showInfoModal) {
					InfoModal(
						modifier = Modifier.fillMaxWidth(0.95f),
						title = stringResource(id = R.string.info_modal_pick_store_overview_title),
						subTitle = stringResource(id = R.string.info_modal_pick_store_overview_message),
						buttonText = "OK",
						buttonClick = { dialogVisibility(false) },
						crossIconClick = { dialogVisibility(false) },
						visualContent = {
							val legendsTextStyle = TextStyle(
								color = BrandColor.GRAY_600,
								fontFamily = FontFamily.ARCHIVO,
								fontWeight = FontWeight.SemiBold,
								textAlign = TextAlign.Center,
								fontSize = 10.sp,
								letterSpacing = 1.5.sp
							)
							DisplayLegends(
								circleSize = 16,
								space = 0,
								items = listOf(
									Legend(BrandColor.GRAY_900, "UNWORKED"),
									Legend(BrandColor.GREEN_400, "WORKED"),
									Legend(BrandColor.YELLOW_400, "BEING WORKED")
								),
								textStyle = legendsTextStyle
							)

							Spacer(modifier = Modifier.height(16.dp))
						},
						dismissOnBackPress = true,
						dismissOnClickOutside = true,
						onDismissRequest = { dialogVisibility(false) }
					)
				}
			}
		} else if (startTaskStatus == GenericViewState.Success) {
			startTaskCompletion.invoke()
		} else if (startTaskStatus == GenericViewState.Failure) {
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
}

@Composable
@PreviewPdt
fun PreviewPickScreen() {
	PickScreen(
		unitsWorked = 1,
		totalUnits = 2,
		hasActiveTask = false,
		onHasActiveTask = {}
	)
}
