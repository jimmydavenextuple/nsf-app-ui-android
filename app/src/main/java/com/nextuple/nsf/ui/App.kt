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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
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
import com.nextuple.nsf.ui.screen.pick.PickScreen
import com.nextuple.nsf.ui.screen.prep.PrepScreen
import com.nextuple.nsf.ui.screen.search.SearchResultsScreen
import com.nextuple.nsf.ui.screen.settings.SettingsScreen
import com.nextuple.nsf.ui.state.ConfigViewModel
import com.nextuple.nsf.ui.state.InfoViewModel
import com.nextuple.nsf.ui.state.OrderViewModel
import com.nextuple.nsf.ui.state.PickViewModel
import com.nextuple.nsf.ui.state.SettingsViewModel
import com.nextuple.nsf.ui.state.UserViewModel
import com.nextuple.nsf.ui.state.UserViewModel.ViewState
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.Haptics
import com.nextuple.nsf.ui.util.PrinterName
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
			iconResId = R.drawable.ic_pick_qr_code_scanner,
			label = "Pick",
			route = Screen.PICK.route,
			count = 0
		),
		AppNavBarItem(
			iconResId = R.drawable.ic_prep_local_mall,
			label = "Prep",
			route = Screen.PREP.route,
			count = 0
		),
		AppNavBarItem(
			iconResId = R.drawable.ic_order_shopping_basket,
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
	pickVM: PickViewModel = hiltViewModel(),
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
	var lastTabNavRoute: String? by remember { mutableStateOf(null) }
	var showToolbarSearch by remember { mutableStateOf(false) }
	var deepLinkUri by remember { mutableStateOf(intentData) }
	val user by userVM.user.collectAsStateWithLifecycle(User())

	val onRefreshData: () -> Unit = {
		infoVM.getStoreOverview()
		infoVM.getDeclineCodes()
	}
	val onTabNav: (route: String) -> Unit = { route ->
		onRefreshData()
		if (route.startsWith(Screen.ORDERS.route) &&
			!lastTabNavRoute.orEmpty().startsWith(Screen.ORDERS.route) &&
			!route.contains("filter=")
		) {
			orderVM.resetFilters()
		}
		// TODO: Remove once HOME is default start destination.
		if (route.startsWith(Screen.PICK.route) &&
			lastTabNavRoute.orEmpty().startsWith(Screen.PICK.route)
		) {
			pickVM.fetchCurrentStep()
		}
		lastTabNavRoute = route
		navCtrl.navigate(route) {
			popUpTo(navCtrl.graph[Screen.PICK.route].id) {
				saveState = true
			}
			launchSingleTop = true
		}
	}

	if (settingsVM.bypassPrinter) {
		Toast.makeText(
			LocalContext.current,
			"Printer Bypass Enabled - Skipping print",
			Toast.LENGTH_LONG
		).show()
	}
	Scaffold(
		topBar = {
			if (userVM.isLoggedIn()) {
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
							Toast.makeText(
								context,
								"Please enter a value to search",
								Toast.LENGTH_LONG
							).show()
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
				startDestination = getStartDestination(userVM.isLoggedIn(), deepLinkUri)
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
					pickVM = pickVM,
					infoVM = infoVM,
					configVM = configVM,
					scanManager = scanManager,
					haptics = haptics,
					onRefreshData = onRefreshData
				)
				composableForPrep(
					configVM = configVM,
					infoVM = infoVM,
					settingsVM = settingsVM,
					scanManager = scanManager,
					onTabNav = onTabNav,
					onRefreshData = onRefreshData
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
			if (userVM.isLoggedIn()) {
				AppNavBar(
					items = NAV_ITEMS,
					isSelected = { route ->
						isRouteInEntry(route, backStackEntry)
					},
					onSelect = onTabNav,
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
			isFormInvalid = userVM.viewState == ViewState.LoginFormValidationError,
			resetIsFormInvalid = userVM::resetFromError,
			errorMessage = userVM.errMsg,
			showProgressBar = userVM.viewState == ViewState.LoggingIn,
			onSubmit = { nodeNo, userId ->
				userVM.login(nodeNo, userId)
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
	pickVM: PickViewModel,
	infoVM: InfoViewModel,
	configVM: ConfigViewModel,
	scanManager: ScanManager,
	haptics: Haptics,
	onRefreshData: () -> Unit
) {
	composable(Screen.PICK.route) {
		val pickDeclineOptions = infoVM.declineCodes?.pickDeclineCodes?.map {
			it.displayName to it.id
		}?.toTypedArray()?.let { linkedMapOf(*it) }
		PickScreen(
			pickVM = pickVM,
			scanManager = scanManager,
			haptics = haptics,
			tasksUnassigned = infoVM.pickTasksUnassigned,
			unitsWorked = infoVM.totalStoreUnitsWorked,
			totalUnits = infoVM.totalStoreUnits,
			inProgressUnits = infoVM.totalStoreUnitsInProgress,
			storeOverviewState = infoVM.storeOverviewState,
			declineCodesState = infoVM.declineCodesState,
			declineModalOptions = pickDeclineOptions,
			onDeclineClick = infoVM::getDeclineCodes,
			isInvalidSymbology = configVM::isInvalidSymbology,
			resetScreen = {
				onRefreshData()
				navCtrl.navigate(Screen.PICK.route) {
					launchSingleTop = true
				}
			}
		)
	}
}

private fun NavGraphBuilder.composableForPrep(
	configVM: ConfigViewModel,
	infoVM: InfoViewModel,
	settingsVM: SettingsViewModel,
	scanManager: ScanManager,
	onTabNav: (route: String) -> Unit,
	onRefreshData: () -> Unit
) {
	composable("${Screen.PREP.route}?frToPack={frToPack}") { navBackStackEntry ->
		val frToPack = navBackStackEntry.arguments?.getString("frToPack")?.ifEmpty { null }
		PrepScreen(
			configVM = configVM,
			settingsVM = settingsVM,
			frToPack = frToPack,

			numPackTasks = infoVM.prepTasksUnassigned,
			scanManager = scanManager,
			onClickPackByOrder = {
				onTabNav("${Screen.ORDERS.route}?filter=PACK")
			},
			resetScreen = onRefreshData
		)
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
//			sddReadyOrder = orderVM.sddOrderByBatchIds,
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

		// TODO: Cleanup params. Order detail response is already being passed in along with its individual properties
		OrderDetailsScreen(
			viewState = orderVM.viewState,
			startPickupViewState = orderVM.startPickupState,
			orderDetailsResponse = orderVM.orderDetailResponse,
			athleteName = athleteDetail?.athleteFullName().orEmpty(),
			athleteProxyName = athleteDetail?.athleteProxyFullName().orEmpty(),
			athletePhoneNumber = StringUtils.toPhoneNumberFormatted(athleteDetail?.athletePhoneNumber.orEmpty()),
			orderNumber = orderDetailsRes?.orderNumber.orEmpty(),
			orderDate = Pair(orderVM.orderDate, orderVM.orderTime),
			expectedDate = orderVM.expectedDate,
			receivedDate = orderVM.receivedDate,
			packedOnDate = Pair(orderVM.packDate, orderVM.packTime),
			pickedUpOnDate = Pair(orderVM.pickedUpOnDate, orderVM.pickedUpOnTime),
			holdingLocation = orderVM.orderDetailResponse?.fulfillmentRequestDetail?.containers?.firstOrNull()?.holdingLocation
				?: "",
			holdSlipZpl = orderVM.holdSlipZpl,
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
			holdSlipState = orderVM.getHoldSlipState,
			resetGetHoldSlipState = orderVM::resetGetHoldSlipState,
			onPrintHoldSlip = { fulfillmentRequestNumber ->
				orderVM.getHoldSlip(fulfillmentRequestNumber = fulfillmentRequestNumber)
			},
			bypassPrinter = settingsVM.bypassPrinter,
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

				navCtrl.navigate("${Screen.PREP.route}?frToPack=$frNo") {
					popUpTo(Screen.HOME.route)
				}
			},
			scanManager = scanManager
		)
	}
}

// todo: screen scope OrderViewModel
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
			holdSlipZpl = orderVM.holdSlipZpl,
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
			resetGetHoldSlipState = orderVM::resetGetHoldSlipState,
			holdSlipState = orderVM.getHoldSlipState,
			bypassPrinter = settingsVM.bypassPrinter,
			ipPrefix = settingsVM.ipPrefix,
			printer = settingsVM.findPrinter(PrinterName.BOPIS),
			printerConnectionState = settingsVM.printerConnectionState,
			onConnectPrinter = settingsVM::connectPrinter,
			onResetPrinter = settingsVM::resetConnectionState
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
			scanManager = scanManager,
			newSearch = {
				navCtrl.popBackStack()
				navCtrl.navigate("${Screen.SEARCH_RESULTS.route}?$it")
			}
		)
	}
}

private fun isRouteInEntry(route: String, entry: NavBackStackEntry?): Boolean =
	entry?.destination?.hierarchy?.any { it.route?.startsWith(route) ?: false } == true

private fun getCurrentRoute(entry: NavBackStackEntry?): String? =
	entry?.destination?.hierarchy?.firstOrNull()?.route
