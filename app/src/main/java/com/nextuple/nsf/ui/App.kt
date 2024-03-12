package com.nextuple.nsf.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.athleteFullName
import com.nextuple.nsf.retrofit.dto.response.athleteProxyFullName
import com.nextuple.nsf.service.dto.User
import com.nextuple.nsf.ui.AppConfig.NAV_ITEMS
import com.nextuple.nsf.ui.component.filter.Filter
import com.nextuple.nsf.ui.nav.AppNavBar
import com.nextuple.nsf.ui.nav.AppNavBarItem
import com.nextuple.nsf.ui.nav.AppTopBar
import com.nextuple.nsf.ui.nav.AppTopBarDropdownMenuItems
import com.nextuple.nsf.ui.nav.NavUtil.getStartDestination
import com.nextuple.nsf.ui.nav.Screen
import com.nextuple.nsf.ui.screen.home.HomeScreen
import com.nextuple.nsf.ui.screen.home.LoginScreen
import com.nextuple.nsf.ui.screen.order.OrderDetailsScreen
import com.nextuple.nsf.ui.screen.order.OrderPickupScreen
import com.nextuple.nsf.ui.screen.order.OrderScreen
import com.nextuple.nsf.ui.screen.pick.PickDetailsScreen
import com.nextuple.nsf.ui.screen.pick.PickScreen
import com.nextuple.nsf.ui.screen.prep.PackType
import com.nextuple.nsf.ui.screen.prep.PrepOrderScreen
import com.nextuple.nsf.ui.screen.prep.PrepScreen
import com.nextuple.nsf.ui.screen.search.SearchResultsScreen
import com.nextuple.nsf.ui.screen.settings.SettingsScreen
import com.nextuple.nsf.ui.state.ConfigViewModel
import com.nextuple.nsf.ui.state.InfoViewModel
import com.nextuple.nsf.ui.state.OrderViewModel
import com.nextuple.nsf.ui.state.PickViewModel
import com.nextuple.nsf.ui.state.PrepViewModel
import com.nextuple.nsf.ui.state.PrinterName
import com.nextuple.nsf.ui.state.SettingsViewModel
import com.nextuple.nsf.ui.state.UserViewModel
import com.nextuple.nsf.ui.state.UserViewModel.ViewState
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.Haptics
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.StringUtils

object AppConfig {
	val NAV_ITEMS = listOf(
		AppNavBarItem(
			iconResId = R.drawable.ic_home,
			label = "Home",
			route = Screen.HOME.route,
			count = 0
		),
		AppNavBarItem(
			iconResId = R.drawable.ic_pick,
			label = "Pick",
			route = Screen.PICK.route,
			count = 0
		),
		AppNavBarItem(
			iconResId = R.drawable.ic_prep,
			label = "Prep",
			route = Screen.PREP.route,
			count = 0
		),
		AppNavBarItem(
			iconResId = R.drawable.ic_orders,
			label = "Orders",
			route = Screen.ORDERS.route,
			count = 0
		)
	)
}

@Composable
fun App(
	infoVM: InfoViewModel,
	userVM: UserViewModel,
	pickVM: PickViewModel,
	prepVM: PrepViewModel,
	orderVM: OrderViewModel,
	settingsVM: SettingsViewModel,
	configVM: ConfigViewModel,
	scanManager: ScanManager,
	haptics: Haptics,
	intentData: Uri?,
	onLoggedIn: () -> Unit
) {
	val navCtrl = rememberNavController()
	val backStackEntry by navCtrl.currentBackStackEntryAsState()
	var showToolbarSearch by remember { mutableStateOf(false) }
	var deepLinkUri by remember { mutableStateOf(intentData) }
	val user by userVM.user.collectAsStateWithLifecycle(User())

	val onRefreshData: () -> Unit = {
		infoVM.getStoreOverview()
		infoVM.getDeclineCodes()
	}
	val onRefreshApp: (route: String) -> Unit = { route ->
		onRefreshData()
		navCtrl.navigate(route) {
			popUpTo(navCtrl.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
		}
	}

	Scaffold(
		topBar = {
			if (user.isLoggedIn()) {
				val context = LocalContext.current

				AppTopBar(
					screen = Screen.getByRoute(getCurrentRoute(backStackEntry).orEmpty()),
					user = user,
					orderType = pickVM.pickTask?.subFulfillmentType,
					items = listOf(
						AppTopBarDropdownMenuItems(
							label = stringResource(id = R.string.dropdown_menu_settings),
							R.drawable.ic_settings
						) {
							navCtrl.navigate(Screen.SETTINGS.route)
						},
						// TODO: Uncomment after implementing.
// 						AppTopBarDropdownMenuItems(
// 							label = stringResource(id = R.string.dropdown_menu_feedback),
// 							R.drawable.ic_feedback
// 						) {},
						AppTopBarDropdownMenuItems(
							label = stringResource(id = R.string.dropdown_menu_logout),
							R.drawable.ic_logout
						) {
							deepLinkUri = null
							userVM.logout()
							navCtrl.popBackStack()
						}
					),
					showSearchBar = showToolbarSearch,
					toggleSearchBar = { showToolbarSearch = it },
					scanManager = scanManager,
					onSearchAction = { searchInput ->
						if (searchInput.isNotEmpty()) {
							navCtrl.navigate("${Screen.SEARCH_RESULTS.route}?$searchInput")
						} else {
							// todo: Confirm text with anna
							Toast.makeText(context, "Please enter a value to search", Toast.LENGTH_LONG).show()
						}
					},
					onBackAction = {
						navCtrl.popBackStack()
					}
				)
			}
		},
		content = { paddingValues ->
			NavHost(
				modifier = Modifier.padding(paddingValues),
				navController = navCtrl,
				startDestination = getStartDestination(user.isLoggedIn(), deepLinkUri)
			) {
				composableForLogin(
					navCtrl = navCtrl,
					infoVM = infoVM,
					userVM = userVM,
					configVM = configVM,
					settingsVM = settingsVM,
					scanManager = scanManager,
					haptics = haptics,
					deepLinkUri = deepLinkUri,
					onLoggedIn = onLoggedIn
				)
				composableForHome()
				composableForPick(
					navCtrl = navCtrl,
					infoVM = infoVM,
					pickVM = pickVM,
					onRefreshData = onRefreshData
				)
				composableForPickDetails(
					navCtrl = navCtrl,
					scanManager = scanManager,
					haptics = haptics,
					infoVM = infoVM,
					configVM = configVM,
					pickVM = pickVM
				)
				composableForPrep(
					navCtrl = navCtrl,
					infoVM = infoVM,
					prepVM = prepVM,
					scanManager = scanManager,
					onRefreshData = onRefreshData
				)
				composableForPrepOrder(
					navCtrl = navCtrl,
					settingsVM = settingsVM,
					prepVM = prepVM,
					configVM = configVM,
					scanManager = scanManager,
					infoVM = infoVM
				)
				composableForOrders(
					navCtrl = navCtrl,
					orderVM = orderVM
				)
				composableForOrderDetails(
					navCtrl = navCtrl,
					orderVM = orderVM,
					settingsVM = settingsVM,
					infoVM = infoVM,
					scanManager = scanManager,
					configVM = configVM
				)
				composableForOrderPickup(
					navCtrl = navCtrl,
					orderVM = orderVM,
					scanManager = scanManager,
					settingsVM = settingsVM
				)
				composableForSettings(
					settingsVM = settingsVM
				)
				composableForSearchResults(
					navCtrl = navCtrl,
					orderVM = orderVM,
					scanManager = scanManager
				)
			}

			if (showToolbarSearch) {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(BrandColor.GRAY_TRANSPARENT)
						.clickable {
							showToolbarSearch = false
						}
				)
			}
		},
		bottomBar = {
			if (user.isLoggedIn()) {
				AppNavBar(
					items = NAV_ITEMS,
					isSelected = { route ->
						isRouteInEntry(route, backStackEntry)
					},
					onSelect = onRefreshApp,
					tasksUnassigned = listOf(
						0, // home tab
						infoVM.pickTasksUnassigned, // pick tab
						infoVM.prepTasksUnassigned, // prep tab
						0 // orders tab
					)
				)
			}
		}
	)
}

private fun NavGraphBuilder.composableForLogin(
	navCtrl: NavController,
	infoVM: InfoViewModel,
	userVM: UserViewModel,
	configVM: ConfigViewModel,
	settingsVM: SettingsViewModel,
	scanManager: ScanManager,
	haptics: Haptics,
	deepLinkUri: Uri?,
	onLoggedIn: () -> Unit
) {
	composable(Screen.LOGIN.route) {
		LoginScreen(
			isInvalid = userVM.viewState == ViewState.LoginError,
			resetIsInvalid = userVM::resetFromError,
			errorMessage = userVM.errMsg,
			showProgressBar = userVM.viewState == ViewState.LoggingIn,
			onSubmitDks = {
				userVM.login(it)
				settingsVM.retrieveSavedPrinters()
			},
			isLoggedIn = userVM.viewState == ViewState.LoggedIn,
			onLoggedIn = {
				configVM.setStoreConfig()
				infoVM.getDeclineCodes()
				onLoggedIn.invoke()
				navCtrl.navigate(getStartDestination(true, deepLinkUri))
				infoVM.getStoreOverview()
			},
			scanManager = scanManager,
			haptics = haptics,
			isInValidSymbology = {
				configVM.isInvalidSymbology(it)
			}
		)
	}
}

private fun NavGraphBuilder.composableForHome() {
	composable(Screen.HOME.route) {
		HomeScreen()
	}
}

private fun NavGraphBuilder.composableForPick(
	navCtrl: NavController,
	infoVM: InfoViewModel,
	pickVM: PickViewModel,
	onRefreshData: () -> Unit
) {
	composable(Screen.PICK.route) {
		PickScreen(
			tasksUnassigned = infoVM.pickTasksUnassigned,
			unitsWorked = infoVM.totalStoreUnitsWorked,
			totalUnits = infoVM.totalStoreUnits,
			inProgressUnits = infoVM.totalStoreUnitsInProgress,
			storeOverviewState = infoVM.storeOverviewState,
			hasActiveTask = pickVM.currentPickItem.value != null,
			onHasActiveTask = {
				navCtrl.navigate(Screen.PICK_DETAILS.route) {
					popUpTo(Screen.HOME.route)
				}
			},
			startTaskStatus = pickVM.startPickState,
			onStartPicking = {
				pickVM.startPick()
			},
			startTaskCompletion = {
				pickVM.resetPickScreen()
				navCtrl.navigate(Screen.PICK_DETAILS.route)
			},
			resetScreen = {
				pickVM.resetPickScreen()
				onRefreshData()
			}
		)
	}
}

private fun NavGraphBuilder.composableForPickDetails(
	navCtrl: NavController,
	scanManager: ScanManager,
	haptics: Haptics,
	infoVM: InfoViewModel,
	configVM: ConfigViewModel,
	pickVM: PickViewModel
) {
	composable(Screen.PICK_DETAILS.route) {
		PickDetailsScreen(
			scanManager = scanManager,
			pickDeclineState = pickVM.pickDeclineState,
			recordPickState = pickVM.recordPickState,
			declineCodesState = infoVM.declineCodesState,
			fulfillmentType = pickVM.pickTask?.fulfillmentType,
			subFulfillmentType = pickVM.pickTask?.subFulfillmentType,
			currentPickTaskItem = pickVM.currentPickItem.value,
			unitsWorked = pickVM.pickTask?.totalWorkedQty ?: 0,
			totalUnits = pickVM.pickTask?.totalQty ?: 0,
			declineModalOptions = infoVM.declineCodes?.pickDeclineCodes?.map {
				it.displayName to it.id
			}?.toTypedArray()?.let { linkedMapOf(*it) },
			onDeclineClick = {
				infoVM.getDeclineCodes()
			},
			onDeclineReasonSelected = { declineReason, declineReasonText ->
				pickVM.declinePick(declineReason, declineReasonText)
			},
			onCheckItemScan = { upc, symbology ->
				if (symbology == null || configVM.isInvalidSymbology(symbology)) {
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
				if (pickVM.currentPickItem.value == null) {
					infoVM.getStoreOverview()
					navCtrl.navigate(Screen.PICK.route) {
						launchSingleTop = true
					}
				}
			},
			onRecordPickCompletion = {
				pickVM.resetRecordPickState()
				if (pickVM.currentPickItem.value == null) {
					infoVM.getStoreOverview()
					navCtrl.navigate(Screen.PICK.route) {
						launchSingleTop = true
					}
				}
			}
		)
	}
}

private fun NavGraphBuilder.composableForPrep(
	navCtrl: NavController,
	infoVM: InfoViewModel,
	prepVM: PrepViewModel,
	scanManager: ScanManager,
	onRefreshData: () -> Unit
) {
	composable(Screen.PREP.route) {
		PrepScreen(
			currentPrepStage = prepVM.currentPrepStage.value,
			navigateToPrepOrder = {
				val frNo = prepVM.stageTask?.fulfillmentRequestNumber?.ifEmpty { null }
					?: prepVM.packTask?.fulfillmentRequestNumber?.ifEmpty { null }

				if (frNo != null) {
					navCtrl.navigate("${Screen.PREP_ORDER.route}/${PackType.ORDER}/$frNo") {
						popUpTo(Screen.HOME.route)
					}
				}
			},
			numPackTasks = infoVM.prepTasksUnassigned,
			scanManager = scanManager,
			onScanGear = { upc ->
				navCtrl.navigate("${Screen.PREP_ORDER.route}/${PackType.GEAR}/$upc") {
					popUpTo(Screen.HOME.route)
				}
			},
			onClickPackByOrder = {
				navCtrl.navigate("${Screen.ORDERS.route}?filter=PACK") {
					popUpTo(Screen.HOME.route)
				}
			},
			resetScreen = {
				onRefreshData()
			}
		)
	}
}

private fun NavGraphBuilder.composableForPrepOrder(
	navCtrl: NavController,
	settingsVM: SettingsViewModel,
	prepVM: PrepViewModel,
	configVM: ConfigViewModel,
	infoVM: InfoViewModel,
	scanManager: ScanManager
) {
	composable("${Screen.PREP_ORDER.route}/{packType}/{data}") { navBackStackEntry ->
		val packType = navBackStackEntry.arguments?.getString("packType")?.let {
			PackType.valueOf(it.trim().uppercase())
		}!!
		val data = navBackStackEntry.arguments?.getString("data") ?: ""

		PrepOrderScreen(
			scanManager = scanManager,
			settingsVM = settingsVM,
			infoVM = infoVM,
			configVM = configVM,
			navCtrl = navCtrl,
			packType = packType,
			data = data
		) {
			prepVM.resetPrepStage()
		}
	}
}

private fun NavGraphBuilder.composableForOrders(
	navCtrl: NavController,
	orderVM: OrderViewModel
) {
	composable("${Screen.ORDERS.route}?filter={filter}") { navBackStackEntry ->
		// Keeping it simple because the current use-case is only one possible filter pre-selected.
		val presetFilter = navBackStackEntry.arguments?.getString("filter")?.ifEmpty { null }
		if (presetFilter != null) {
			orderVM.resetFilters()
			orderVM.setFilter(Filter(text = presetFilter, isSelected = true))

			// Nav right back without arguments to "clear" it. Otherwise, the pack persists
			// even when removed until next Orders tab refresh.
			navCtrl.navigate(Screen.ORDERS.route) {
				popUpTo(Screen.HOME.route)
			}
		}

		OrderScreen(
			viewState = orderVM.viewState,
			orderDetailsState = orderVM.orderDetailsState,
			getOrders = { query ->
				orderVM.getOrders(query = query)
			},
			getOrderDetails = { fulfillmentRequestNumber ->
				orderVM.getOrderDetails(
					fulfillmentRequestNumber = fulfillmentRequestNumber
				)
			},
			getOrderDetailsCompletion = {
				navCtrl.navigate(Screen.ORDERS_DETAILS.route)
				orderVM.resetOrderDetailsState()
			},
			readyOrders = orderVM.readyOrders,
			inProgressOrders = orderVM.inProgressOrders,
			orderTypeFilters = orderVM.orderTypeFilters,
			orderStatusFilters = orderVM.orderStatusFilters,
			selectedTab = orderVM.selectedTab,
			onSelectTab = orderVM::setTab,
			onApplyFilters = orderVM::setFilters
		)
	}
}

private fun NavGraphBuilder.composableForOrderDetails(
	navCtrl: NavController,
	orderVM: OrderViewModel,
	settingsVM: SettingsViewModel,
	infoVM: InfoViewModel,
	scanManager: ScanManager,
	configVM: ConfigViewModel
) {
	composable(Screen.ORDERS_DETAILS.route) {
		val orderDetailsRes = orderVM.orderDetailResponse
		val athleteDetail = orderDetailsRes?.athleteDetail

		// TODO: this is a quick fix until the vm gets refactored
		settingsVM.resetHoldSlipPrintState()

		// TODO: Cleanup params. Order detail response is already being passed in along with its individual properties
		OrderDetailsScreen(
			viewState = orderVM.viewState,
			startPickupViewState = orderVM.startPickupState,
			orderDetailsResponse = orderVM.orderDetailResponse,
			athleteName = athleteDetail?.athleteFullName().orEmpty(),
			athleteProxyName = athleteDetail?.athleteProxyFullName().orEmpty(),
			athletePhoneNumber = StringUtils.toPhoneNumberFormatted(athleteDetail?.athletePhoneNumber.orEmpty()),
			orderNumber = orderDetailsRes?.orderNumber.orEmpty(),
			orderDate = orderVM.orderDate,
			expectedDate = orderVM.expectedDate,
			receivedDate = orderVM.receivedDate,
			packedOnDate = orderVM.packedOnDate,
			holdingLocation = orderVM.orderDetailResponse?.fulfillmentRequestDetail?.containers?.firstOrNull()?.holdingLocation ?: "",
			holdingAreas = configVM.getHoldingLocations(),
			declineModalOptions = infoVM.declineCodes?.pickupDeclineCodes,
			onStartPickup = { fulfillmentRequestNumber ->
				orderVM.startPickup(fulfillmentRequestNumber = fulfillmentRequestNumber)
			},
			onPickupExtend = { fulfillmentRequestNumber ->
				orderVM.pickupExtend(fulfillmentRequestNumber = fulfillmentRequestNumber)
			},
			onPickupRemoveCheckIn = { taskId ->
				orderVM.pickupRemoveCheckIn(taskId = taskId)
			},
			onStartPickupCompletion = {
				navCtrl.navigate(Screen.ORDERS_PICKUP.route)
				orderVM.resetStarPickupState()
			},
			holdSlipState = orderVM.holdSlipState,
			onPrintHoldSlip = { fulfillmentRequestNumber ->
				orderVM.getHoldSlip(fulfillmentRequestNumber = fulfillmentRequestNumber)
			},
			printHoldSlipState = settingsVM.holdSlipPrintState,
			onPrintHoldSlipSuccessCallBack = {
				orderVM.resetHoldSlipState()
				settingsVM.printBOPISHoldSlip(orderVM.holdSlipZpl!!)
			},
			onResetPrintHoldSlip = {
				settingsVM.resetHoldSlipPrintState()
			},
			ipPrefix = settingsVM.ipPrefix,
			printer = settingsVM.findPrinter(PrinterName.BOPIS),
			printerConnectionState = settingsVM.printerConnectionState,
			onConnectPrinter = settingsVM::connectPrinter,
			onResetPrinter = {
				settingsVM.resetConnectionState()
			},
			cancelReasonData = orderVM.cancelReasonData,
			onCancelOrder = { fulfillmentRequestNumber, declinedReason, shouldTranslateReason ->
				orderVM.cancelOrder(
					fulfillmentRequestNumber = fulfillmentRequestNumber,
					declinedReason = declinedReason,
					shouldTranslateReason = shouldTranslateReason
				)
			},
			onCancelComplete = {
				orderVM.resetCancelReasonData()
				navCtrl.navigate(Screen.ORDERS.route)
			},
			onCancelAgedError = orderVM::cancelAgedOrderError,
			onLocationChange = { containerId, holdingLocation ->
				orderVM.recordHoldingLocation(
					containerId,
					holdingLocation
				)
			},
			scanLocationState = orderVM.scanLocationState,
			resetScanLocationState = orderVM::resetScanLocationState,
			onPackOrder = { frNo ->
				navCtrl.navigate("${Screen.PREP_ORDER.route}/${PackType.ORDER}/$frNo") {
					popUpTo(Screen.HOME.route)
				}
			},
			scanManager = scanManager
		)
	}
}

private fun NavGraphBuilder.composableForOrderPickup(
	navCtrl: NavController,
	orderVM: OrderViewModel,
	scanManager: ScanManager,
	settingsVM: SettingsViewModel
) {
	composable(Screen.ORDERS_PICKUP.route) {
		OrderPickupScreen(
			scanManager = scanManager,
			completeOrderPickupState = orderVM.completeOrderPickupState,
			orderNumber = orderVM.orderDetailResponse?.orderNumber,
			athleteDetail = orderVM.orderDetailResponse?.athleteDetail,
			checkInDetail = orderVM.orderDetailResponse?.athleteCheckInDetail,
			frDetail = orderVM.orderDetailResponse?.fulfillmentRequestDetail,
			onOrderPickupClicked = { taskId ->
				orderVM.completePickupTask(taskId = taskId)
			},
			onOrderPickupSuccess = {
				orderVM.resetCompleteOrderPickState()
				navCtrl.navigate(Screen.ORDERS.route) {
					popUpTo(Screen.ORDERS.route)
				}
			},
			onScanSuccess = orderVM::holdSuccessSlipScan,
			scanHoldSlipState = orderVM.holdSlipScanState,
			onResetHoldSlipScan = orderVM::resetHoldSlipScanState,
			onPrintHoldSlip = { fulfillmentRequestNumber ->
				orderVM.getHoldSlip(fulfillmentRequestNumber = fulfillmentRequestNumber)
			},
			printHoldSlipState = settingsVM.holdSlipPrintState,
			onPrintHoldSlipSuccessCallBack = {
				orderVM.resetHoldSlipState()
				settingsVM.printBOPISHoldSlip(orderVM.holdSlipZpl!!)
			},
			holdSlipState = orderVM.holdSlipState,
			onResetPrintHoldSlip = {
				settingsVM.resetHoldSlipPrintState()
			},
			ipPrefix = settingsVM.ipPrefix,
			printer = settingsVM.findPrinter(PrinterName.BOPIS),
			printerConnectionState = settingsVM.printerConnectionState,
			onConnectPrinter = settingsVM::connectPrinter,
			onResetPrinter = {
				settingsVM.resetConnectionState()
			}
		)
	}
}

private fun NavGraphBuilder.composableForSettings(
	settingsVM: SettingsViewModel
) {
	composable(Screen.SETTINGS.route) {
		val context = LocalContext.current

		SettingsScreen(
			printersList = settingsVM.printersList,
			printerConnectionState = settingsVM.printerConnectionState,
			ipPrefix = settingsVM.ipPrefix,
			isDebug = BuildConfig.DEBUG,
			onConnectPrinter = settingsVM::connectPrinter,
			onDisConnectPrinter = settingsVM::disConnectPrinter,
			onReset = {
				settingsVM.resetConnectionState()
			},
			togglePrinterBypass = {
				val bypass = settingsVM.toggleBypassPrinter()

				Toast.makeText(
					context,
					"PrinterBypass: $bypass",
					Toast.LENGTH_SHORT
				).show()
			}
		)
	}
}

private fun NavGraphBuilder.composableForSearchResults(
	navCtrl: NavController,
	orderVM: OrderViewModel,
	scanManager: ScanManager
) {
	composable("${Screen.SEARCH_RESULTS.route}?{searchInputText}") { navBackStackEntry ->
		val searchInputText = navBackStackEntry.arguments?.getString("searchInputText") ?: ""

		SearchResultsScreen(
			viewState = orderVM.viewState,
			orderDetailsState = orderVM.orderDetailsState,
			searchInput = searchInputText,
			getOrders = {
				orderVM.getOrders(query = it)
			},
			orderResults = orderVM.readyOrders + orderVM.inProgressOrders,
			getOrderDetails = { fulfillmentRequestNumber ->
				orderVM.getOrderDetails(
					fulfillmentRequestNumber = fulfillmentRequestNumber
				)
			},
			getOrderDetailsCompletion = {
				navCtrl.navigate(Screen.ORDERS_DETAILS.route)
				orderVM.resetOrderDetailsState()
			},
			scanManager = scanManager
		)
	}
}

private fun isRouteInEntry(route: String, entry: NavBackStackEntry?): Boolean =
	entry?.destination?.hierarchy?.any { it.route?.startsWith(route) ?: false } == true

private fun getCurrentRoute(entry: NavBackStackEntry?): String? =
	entry?.destination?.hierarchy?.firstOrNull()?.route
