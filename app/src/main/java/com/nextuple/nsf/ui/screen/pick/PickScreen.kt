package com.nextuple.nsf.ui.screen.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.CircularProgressBar
import com.nextuple.nsf.ui.common.DisplayLegends
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.Legend
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.Tab
import com.nextuple.nsf.ui.component.EmptyStateScreen
import com.nextuple.nsf.ui.state.BarcodeScannerViewModel
import com.nextuple.nsf.ui.state.PickViewModel
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.Haptics
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager

enum class PickScreenTab(val displayName: String) {
	PICKUP("PICKUP"),
	SFS("SFS")
}

/**
 * @param declineModalOptions a linked map with display string as key and decline reason as value
 */
@Composable
fun PickScreen(
	barcodeScannerVM: BarcodeScannerViewModel,
	pickVM: PickViewModel,
	scanManager: ScanManager,
	haptics: Haptics,
	tasksUnassigned: Int? = null,
	unitsWorked: Int? = null,
	totalUnits: Int? = null,
	inProgressUnits: Int? = null,
	storeOverviewState: GenericViewState = GenericViewState.Idle,
	declineCodesState: GenericViewState,
	declineModalOptions: LinkedHashMap<String, String>?,
	onDeclineClick: () -> Unit,
	isInvalidSymbology: (symbology: String) -> Boolean,
	resetScreen: () -> Unit = {}
) {
	val ctx = LocalContext.current

	LaunchedEffect(Unit) {
		pickVM.fetchCurrentStep()
	}
	when (pickVM.viewState) {
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
				// TODO: Restore when Pick is no longer Home.
// 				Toast.makeText(
// 					ctx,
// 					"Failure retrieving user pick tasks.",
// 					Toast.LENGTH_SHORT
// 				).show()
				pickVM.resetPickState()
			}
		}

		else -> when {
			pickVM.pickTask != null -> {
				pickVM.updateCurrentPickItem()
				PickDetailsScreen(
					barcodeScannerVM = barcodeScannerVM,
					pickVM = pickVM,
					scanManager = scanManager,
					pickDeclineState = pickVM.pickDeclineState,
					recordPickState = pickVM.recordPickState,
					declineCodesState = declineCodesState,
					fulfillmentType = pickVM.pickTask?.fulfillmentType,
					subFulfillmentType = pickVM.pickTask?.subFulfillmentType,
					currentPickTaskItem = pickVM.currentPickItem,
					unitsWorked = pickVM.pickTask?.totalWorkedQty ?: 0,
					totalUnits = pickVM.pickTask?.totalQty ?: 0,
					declineModalOptions = declineModalOptions,
					onDeclineClick = onDeclineClick,
					onDeclineReasonSelected = { declineReason, declineReasonText ->
						pickVM.declinePick(declineReason, declineReasonText)
					},
					onCheckItemScan = { upc, symbology ->
						if (symbology == null || isInvalidSymbology(symbology)) {
							haptics.boop()
							return@PickDetailsScreen false
						}
						if (!pickVM.matchUpc(upc)) {
							haptics.boop()
							haptics.vibrate(1000)
							return@PickDetailsScreen false
						}
						return@PickDetailsScreen true
					},
					onItemPick = { upc, pickLocation ->
						if (!pickVM.pickItem(upc, pickLocation)) {
							haptics.boop()
							haptics.vibrate(1000)
						}
					},
					onDeclineCompletion = {
						pickVM.resetPickDeclineState()
						if (pickVM.currentPickItem == null) {
							pickVM.resetPickState()
							resetScreen()
						}
					},
					onRecordPickCompletion = {
						pickVM.resetRecordPickState()
						if (pickVM.currentPickItem == null) {
							pickVM.resetPickState()
							resetScreen()
						}
					}
				)
			}

			else -> {
				PickLanding(
					tasksUnassigned = tasksUnassigned,
					unitsWorked = unitsWorked,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits,
					storeOverviewState = storeOverviewState,
					startTaskStatus = pickVM.startPickState,
					onStartPicking = pickVM::startPick,
					startTaskCompletion = pickVM::resetPickScreen,
					resetScreen = {
						pickVM.resetPickScreen()
						resetScreen()
					}
				)
			}
		}
	}
}

@Composable
fun PickLanding(
	tasksUnassigned: Int?,
	unitsWorked: Int?,
	totalUnits: Int?,
	inProgressUnits: Int?,
	storeOverviewState: GenericViewState,
	startTaskStatus: GenericViewState,
	onStartPicking: () -> Unit,
	startTaskCompletion: () -> Unit,
	resetScreen: () -> Unit
) {
	var selectedTab by rememberSaveable { mutableStateOf(PickScreenTab.PICKUP) }

	Column {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(40.dp)
		) {
			Tab(
				modifier = Modifier.weight(1f),
				title = PickScreenTab.PICKUP.displayName,
				count = tasksUnassigned ?: 0,
				isSelected = selectedTab == PickScreenTab.PICKUP
			) {
				selectedTab = PickScreenTab.PICKUP
				resetScreen()
			}
			Tab(
				modifier = Modifier.weight(1f),
				title = PickScreenTab.SFS.displayName,
				count = 0, // TODO: fix for sfs orders
				isSelected = selectedTab == PickScreenTab.SFS
			) {
				selectedTab = PickScreenTab.SFS
				resetScreen()
			}
		}
		if (selectedTab == PickScreenTab.PICKUP) {
			PickupTabContainer(
				tasksUnassigned,
				storeOverviewState,
				startTaskStatus,
				unitsWorked,
				totalUnits,
				inProgressUnits,
				onStartPicking,
				startTaskCompletion,
				resetScreen
			)
		} else {
			SfsTabContainer()
		}
	}
}

@Composable
private fun PickupTabContainer(
	tasksUnassigned: Int?,
	storeOverviewState: GenericViewState,
	startTaskStatus: GenericViewState,
	unitsWorked: Int?,
	totalUnits: Int?,
	inProgressUnits: Int?,
	onStartPicking: () -> Unit,
	startTaskCompletion: () -> Unit,
	resetScreen: () -> Unit
) {
	var showInfoModal by remember { mutableStateOf(false) }

	fun dialogVisibility(visibility: Boolean) {
		showInfoModal = visibility
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
					modifier = Modifier
						.width(width = 200.dp),
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
								fontWeight = FontWeight.SemiBold,
								textAlign = TextAlign.Center,
								fontSize = 10.sp,
								letterSpacing = 1.5.sp
							)
							DisplayLegends(
								circleSize = 16,
								space = 0,
								items = listOf(
									Legend(BrandColor.GRAY_400, "UNWORKED"),
									Legend(BrandColor.DARK_BLUE, "WORKED"),
									Legend(BrandColor.YELLOW_NT, "BEING WORKED")
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
private fun SfsTabContainer() {
	Box(
		modifier = Modifier
			.fillMaxHeight()
			.background(BrandColor.WHITE),
		contentAlignment = Alignment.Center
	) {
		EmptyStateScreen(
			title = stringResource(id = R.string.pick_under_construction_title),
			body = stringResource(id = R.string.pick_under_construction_body),
			imageVector = ImageVector.vectorResource(id = R.drawable.under_construction)
		)
	}
}

@Composable
@PreviewPdt
fun PreviewPickLanding() {
	PickLanding(
		tasksUnassigned = null,
		unitsWorked = 1,
		totalUnits = 2,
		inProgressUnits = null,
		storeOverviewState = GenericViewState.Idle,
		startTaskStatus = GenericViewState.Idle,
		onStartPicking = {},
		startTaskCompletion = {},
		resetScreen = {}
	)
}

@Composable
@PreviewPdt
fun PreviewPickLandingStartPicking() {
	PickLanding(
		tasksUnassigned = null,
		unitsWorked = 1,
		totalUnits = 1,
		inProgressUnits = null,
		storeOverviewState = GenericViewState.Success,
		startTaskStatus = GenericViewState.Success,
		onStartPicking = {},
		startTaskCompletion = {},
		resetScreen = {}
	)
}

@Composable
@PreviewPdt
fun PreviewSfsTabScreen() {
	SfsTabContainer()
}
