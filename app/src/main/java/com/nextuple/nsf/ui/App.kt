package com.nextuple.nsf.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.nextuple.nsf.R
import com.nextuple.nsf.service.DeviceService
import com.nextuple.nsf.ui.AppConfig.NAV_ITEMS
import com.nextuple.nsf.ui.nav.AppNavBar
import com.nextuple.nsf.ui.nav.AppNavBarItem
import com.nextuple.nsf.ui.nav.AppTopBar
import com.nextuple.nsf.ui.nav.AppTopBarDropdownMenuItems
import com.nextuple.nsf.ui.nav.Route
import com.nextuple.nsf.ui.screen.home.HomeScreen
import com.nextuple.nsf.ui.screen.home.LoginScreen
import com.nextuple.nsf.ui.screen.order.OrderDetails
import com.nextuple.nsf.ui.screen.order.OrderScreen
import com.nextuple.nsf.ui.screen.order.PickOrderScreen
import com.nextuple.nsf.ui.screen.pick.PickDetailsScreen
import com.nextuple.nsf.ui.screen.pick.PickScreen
import com.nextuple.nsf.ui.screen.prep.PackDetailsScreen
import com.nextuple.nsf.ui.screen.prep.PrepScreen
import com.nextuple.nsf.ui.screen.prep.StageDetailsScreen
import com.nextuple.nsf.ui.screen.settings.SettingsScreen
import com.nextuple.nsf.ui.state.AppViewModel
import com.nextuple.nsf.ui.state.OrderViewModel
import com.nextuple.nsf.ui.state.PickViewModel
import com.nextuple.nsf.ui.state.PrepViewModel
import com.nextuple.nsf.ui.state.SettingsViewModel
import com.nextuple.nsf.ui.state.UserViewModel
import com.nextuple.nsf.ui.state.UserViewModel.ViewState
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.Haptics
import com.nextuple.nsf.ui.util.PICK_ITEM_SYMBOLOGY_PREFIXES
import com.nextuple.nsf.ui.util.ScanManager

object AppConfig {
	val NAV_ITEMS = listOf(
		AppNavBarItem(
			iconResId = R.drawable.ic_home,
			label = "Home",
			route = Route.HOME,
			count = 0
		),
		AppNavBarItem(
			iconResId = R.drawable.ic_pick,
			label = "Pick",
			route = Route.PICK,
			count = 0
		),
		AppNavBarItem(
			iconResId = R.drawable.ic_prep,
			label = "Prep",
			route = Route.PREP,
			count = 0
		),
		AppNavBarItem(
			iconResId = R.drawable.ic_orders,
			label = "Orders",
			route = Route.ORDERS,
			count = 0
		)
	)
}

private val APP_TOP_BAR_COLOR = BrandColor.BLUE_800_NT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
	appVM: AppViewModel,
	userVM: UserViewModel,
	pickVM: PickViewModel,
	prepVM: PrepViewModel,
	orderVM: OrderViewModel,
	settingsVM: SettingsViewModel,
	scanManager: ScanManager,
	haptics: Haptics,
	onLoggedIn: () -> Unit,
	loadPickScreenOnNotificationTap: Boolean,
	deviceService: DeviceService
) {
	val navCtrl = rememberNavController()
	val backStackEntry by navCtrl.currentBackStackEntryAsState()
	val isHomeScreen = isRouteInEntry(Route.HOME, backStackEntry)

	val isLoggedIn = userVM.user != null
	val dks = userVM.user?.dks.orEmpty()

	Scaffold(
		topBar = {
			if (isLoggedIn) {
				AppTopBar(
					backgroundColor = APP_TOP_BAR_COLOR,
					title = if (isHomeScreen) {
						"NSF"
					} else {
						getAppTopBarTitle(getCurrentRoute(backStackEntry).orEmpty())
					},
					userLastName = userVM.user?.lastName.orEmpty(),
					dks = userVM.user?.dks.orEmpty(),
					items = listOf(
						AppTopBarDropdownMenuItems(
							label = stringResource(id = R.string.dropdown_menu_settings),
							R.drawable.ic_settings
						) {
							navCtrl.navigate(Route.SETTINGS)
						},
						AppTopBarDropdownMenuItems(
							label = stringResource(id = R.string.dropdown_menu_feedback),
							R.drawable.ic_feedback
						) {},
						AppTopBarDropdownMenuItems(
							label = stringResource(id = R.string.dropdown_menu_logout),
							R.drawable.ic_logout
						) {
							userVM.logout()
							pickVM.onLogout()
							navCtrl.popBackStack()
							//Firebase topic unsubscription
							try {
								FirebaseMessaging.getInstance().unsubscribeFromTopic(deviceService.getStore().id.toString() + "_notification_topic")
								System.out.println("Unsubscribed from " + deviceService.getStore().id.toString() + "_notification_topic\"!")
							} catch (e: Exception) {
								System.out.println("Failed to unsubscribe!")
							}
						}
					)
				)
			}
		},
		content = { paddingValues ->
			NavHost(
				modifier = Modifier.padding(paddingValues),
				navController = navCtrl,
				startDestination = if (isLoggedIn)
					if(loadPickScreenOnNotificationTap)
						Route.PICK
					else  Route.HOME
				else Route.LOGIN
			) {
				composableForLogin(
					navCtrl = navCtrl,
					appVM = appVM,
					userVM = userVM,
					pickVM = pickVM,
					prepVM = prepVM,
					onLoggedIn,
					deviceService = deviceService
				)
				composableForHome(
					scanManager = scanManager,
					onSearchClick = { homeSearchInput ->
						navCtrl.navigate("${Route.ORDERS}?$homeSearchInput")
					}
				)
				composable(Route.PICK) {
					PickScreen(
						tasksUnassigned = pickVM.pickTasksUnassigned.value,
						unitsWorked = pickVM.storeOverview?.pickOverview?.unitsWorked,
						totalUnits = pickVM.totalStoreUnits.value,
						inProgressUnits = pickVM.storeOverview?.pickOverview?.unitsInProgress,
						storeOverviewState = pickVM.storeOverviewState,
						hasActiveTask = pickVM.currentPickTask.value != null,
						onHasActiveTask = {
							navCtrl.navigate(Route.PICK_DETAILS) {
								popUpTo(Route.HOME)
							}
						},
						startTaskStatus = pickVM.startPickState,
						onStartPicking = {
							pickVM.startPick(dks)
						},
						startTaskCompletion = {
							pickVM.resetStartPickState()
							navCtrl.navigate(Route.PICK_DETAILS)
						},
						resetScreen = {
							pickVM.resetPickScreen()
						}
					)
				}
				composable(Route.PREP) {
					PrepScreen(
						storeOverViewState = appVM.storeOverviewState,
						startTaskStatus = prepVM.startPackState,
						currentPrepStage = prepVM.currentPreStage.value,
						navigateToStage2 = {
							navCtrl.navigate(Route.STAGE_ORDER) {
								popUpTo(Route.HOME)
							}
						},
						navigateToStage1 = {
							navCtrl.navigate(Route.PACK_ORDER) {
								popUpTo(Route.HOME)
							}
						},
						prepTasks = prepVM.storeOverview?.prepOverview?.prepTasks,
						onStartPack = { taskId ->
							prepVM.startPack(taskId = taskId, dks = dks)
						},
						startTaskCompletion = {
							prepVM.resetPackState()
							navCtrl.navigate(Route.PACK_ORDER) {
								popUpTo(Route.HOME)
							}
						},
						onBenchActionCallback = {
							prepVM.resetPackState()
							appVM.getStoreOverview(dks) { storeOverviewState, storeOverview ->
								pickVM.onStoreOverViewCompletion(
									storeOverviewState,
									storeOverview
								)
								prepVM.onStoreOverViewCompletion(storeOverview)
							}
						}
					)
				}
				composable("${Route.ORDERS}?{searchInputText}") { navBackStackEntry ->
					val searchInputText =
						navBackStackEntry.arguments?.getString("searchInputText") ?: ""
					OrderScreen(
						scanManager = scanManager,
						viewState = orderVM.viewState,
						orderDetailsState = orderVM.orderDetailsState,
						homeSearchInput = searchInputText,
						getOrders = { query, pastDays ->
							orderVM.getOrders(query = query, pastDays = pastDays, dks = userVM.user?.dks ?: "")
						},
						getOrderDetails = { fulfillmentRequestNumber ->
							orderVM.getOrderDetails(
								fulfillmentRequestNumber = fulfillmentRequestNumber,
								dks = userVM.user?.dks ?: ""
							)
						},
						getOrderDetailsCompletion = {
							navCtrl.navigate(Route.ORDERS_DETAILS)
							orderVM.resetOrderDetailsState()
						},
						allOrderList = orderVM.orderList,
						pickupOrderList = orderVM.pickUpOrderList.value
					)
				}
				composable(Route.ORDERS_DETAILS) {
					OrderDetails(
						viewState = orderVM.viewState,
						startPickupViewState = orderVM.startPickupState,
						orderDetailsResponse = orderVM.orderDetailResponse,
						onBackButtonClick = {
							navCtrl.popBackStack()
						},
						onStartPickup = { fulfillmentRequestNumber ->
							orderVM.startPickup(dks = userVM.user?.dks ?: "", fulfillmentRequestNumber = fulfillmentRequestNumber)
						},
						onPickupExtend = { fulfillmentRequestNumber ->
							orderVM.pickupExtend(dks = userVM.user?.dks ?: "", fulfillmentRequestNumber = fulfillmentRequestNumber)
						},
						onPickupRemoveCheckIn = { taskId ->
							orderVM.pickupRemoveCheckIn(dks = userVM.user?.dks ?: "", taskId = taskId)
						},
						onStartPickupCompletion = {
							navCtrl.navigate(Route.ORDERS_PICKUP)
							orderVM.resetStarPickupState()
						}
					)
				}

				composable(Route.ORDERS_PICKUP) {
					PickOrderScreen(
						completeOrderPickupState = orderVM.completeOrderPickupState,
						orderDetails = orderVM.orderDetailResponse,
						onBackButtonClick = {
							navCtrl.popBackStack()
						},
						onOrderPickupClicked = { taskId ->
							orderVM.completePickupTask(dks = userVM.user?.dks ?: "", taskId = taskId)
						},
						onOrderPickupSuccess = {
							orderVM.resetCompleteOrderPickState()
							navCtrl.navigate(Route.ORDERS) {
								popUpTo(Route.ORDERS)
							}
						}
					)
				}
				composable(Route.PICK_DETAILS) {
					PickDetailsScreen(
						scanManager = scanManager,
						progressBarBackgroundColor = APP_TOP_BAR_COLOR,
						pickDeclineState = pickVM.pickDeclineState,
						recordPickState = pickVM.recordPickState,
						declineCodesState = pickVM.declineCodesState,
						currentPickTaskItem = pickVM.currentPickItem.value,
						unitsWorked = pickVM.pickTask?.totalWorkedQty ?: 0,
						totalUnits = pickVM.pickTask?.totalQty ?: 0,
						declineModalOptions = pickVM.declineCodes?.pickDeclineCodes?.map { it.displayName },
						onDeclineClick = {
							pickVM.getDeclineCodes(dks)
						},
						onDeclineReasonSelected = { declineReason ->
							pickVM.declinePick(declineReason, dks)
						},
						onItemPick = { upc, symbology ->
							if (symbology == null ||
								PICK_ITEM_SYMBOLOGY_PREFIXES.none {
									symbology.equals(it, ignoreCase = true)
								}
							) {
								haptics.boop()
								return@PickDetailsScreen
							}

							if (!pickVM.pickItem(upc, dks)) {
								haptics.boop()
								haptics.vibrate(1000)
							}
						},
						onDeclineCompletion = {
							pickVM.resetPickDeclineState()
							if (pickVM.currentPickItem.value == null) {
								appVM.getStoreOverview(dks) { storeOverviewState, storeOverview ->
									pickVM.onStoreOverViewCompletion(
										storeOverviewState,
										storeOverview
									)
									prepVM.onStoreOverViewCompletion(storeOverview)
								}
								navCtrl.navigate(Route.PICK) {
									launchSingleTop = true
								}
							}
						},
						onRecordPickCompletion = {
							pickVM.resetRecordPickState()
							if (pickVM.currentPickItem.value == null) {
								appVM.getStoreOverview(dks) { storeOverviewState, storeOverview ->
									pickVM.onStoreOverViewCompletion(
										storeOverviewState,
										storeOverview
									)
									prepVM.onStoreOverViewCompletion(storeOverview)
								}
								navCtrl.navigate(Route.PICK) {
									launchSingleTop = true
								}
							}
						}
					)
				}
				composable(Route.PACK_ORDER) {
					PackDetailsScreen(
						progressBarBackgroundColor = APP_TOP_BAR_COLOR,
						holdSlipState = prepVM.holdSlipState,
						athlete = prepVM.athlete.value,
						orderNum = prepVM.orderNum.value,
						packItems = prepVM.packItems.value,
						onPrintHoldSlip = {
							prepVM.packAndGetHoldSlip(dks = dks)
						},
						onPrintHoldSlipSuccessCallBack = {
							prepVM.resetHoldSlipState()
							try {
								settingsVM.printBOPISHoldSlip(prepVM.stageTask!!.holdSlipZPL)
							} catch (e: Exception) {
								Log.e("nullHoldSlip", "Hold Slip not Printed: hold slip info not found")
							}
							navCtrl.navigate(Route.STAGE_ORDER) {
								popUpTo(Route.HOME)
							}
						},
						resetScreen = {
							prepVM.resetHoldSlipState()
						}
					)
				}
				composable(Route.STAGE_ORDER) {
					StageDetailsScreen(
						progressBarBackgroundColor = APP_TOP_BAR_COLOR,
						holdLocationState = prepVM.holdLocationState,
						storeOverviewState = appVM.storeOverviewState,
						scanManager = scanManager,
						athlete = prepVM.athlete.value,
						orderNum = prepVM.orderNum.value,
						stageTask = prepVM.stageTask,
						onRecordHoldingLocation = { containerId, holdingLocation ->
							prepVM.recordHoldingLocation(
								containerId,
								holdingLocation,
								dks
							)
						},
						onStageCompletionCallBack = {
							appVM.getStoreOverview(dks) { storeOverviewState, storeOverview ->
								pickVM.onStoreOverViewCompletion(
									storeOverviewState,
									storeOverview
								)
								prepVM.onStoreOverViewCompletion(storeOverview)
								prepVM.resetHoldLocationState()
								navCtrl.navigate(Route.PREP) {
									popUpTo(Route.HOME)
								}
							}
						},
						onReprintHoldSlip = {
							try {
								settingsVM.printBOPISHoldSlip(prepVM.stageTask!!.holdSlipZPL)
							} catch (e: Exception) {
								Log.e("nullHoldSlip", "Hold Slip not Printed: hold slip info not found")
							}
						}
					)
				}
				composable(Route.SETTINGS) {
					SettingsScreen(
						printersList = settingsVM.printersList,
						printerConnectionState = settingsVM.printerConnectionState,
						ipPrefix = settingsVM.ipPrefix,
						onConnectPrinter = settingsVM::connectPrinter,
						onDisConnectPrinter = settingsVM::disConnectPrinter,
						onReset = {
							settingsVM.resetConnectionState()
						},
						onBackButtonClick = {
							navCtrl.popBackStack()
						}
					)
				}
			}
		},
		bottomBar = {
			if (isLoggedIn) {
				AppNavBar(
					items = NAV_ITEMS,
					isSelected = { route ->
						isRouteInEntry(route, backStackEntry)
					},
					onSelect = { route ->
						appVM.getStoreOverview(dks) { storeOverviewState, storeOverview ->
							pickVM.onStoreOverViewCompletion(storeOverviewState, storeOverview)
							prepVM.onStoreOverViewCompletion(storeOverview)
						}
						pickVM.getDeclineCodes(dks)
						navCtrl.navigate(route) {
							popUpTo(navCtrl.graph.findStartDestination().id) {
								saveState = true
							}
							launchSingleTop = true
							// 	restoreState = true
						}
					},
					tasksUnassigned = listOf(
						0, // home tab
						pickVM.pickTasksUnassigned.value, // pick tab
						pickVM.prepTasksUnassigned.value, // prep tab
						0 // orders tab
					)
				)
			}
		}
	)
}

private fun NavGraphBuilder.composableForLogin(
	navCtrl: NavController,
	appVM: AppViewModel,
	userVM: UserViewModel,
	pickVM: PickViewModel,
	prepVM: PrepViewModel,
	onLoggedIn: () -> Unit,
	deviceService: DeviceService
) {
	composable(Route.LOGIN) {
		LoginScreen(
			isInvalid = userVM.viewState == ViewState.LoginError,
			resetIsInvalid = userVM::resetFromError,
			errorMessage = userVM.errMsg,
			showProgressBar = userVM.viewState == ViewState.LoggingIn,
			onSubmitDks = userVM::login,
			isLoggedIn = userVM.viewState == ViewState.LoggedIn,
			onLoggedIn = {
				pickVM.getDeclineCodes(userVM.user?.dks.orEmpty())
				onLoggedIn.invoke()
				navCtrl.navigate(Route.HOME)
				appVM.getStoreOverview(userVM.user?.dks.orEmpty()) { storeOverviewState, storeOverview ->
					pickVM.onStoreOverViewCompletion(storeOverviewState, storeOverview)
					prepVM.onStoreOverViewCompletion(storeOverview)

					//Firebase topic subscription
					try {
						FirebaseMessaging.getInstance().subscribeToTopic(deviceService.getStore().id.toString() + "_notification_topic")
						System.out.println("Subscribed to " + deviceService.getStore().id.toString() + "_notification_topic\"!")
					} catch (e: Exception) {
						System.out.println("Failed to subscribe!")
					}
				}
			}
		)
	}
}

private fun NavGraphBuilder.composableForHome(scanManager: ScanManager, onSearchClick: (String) -> Unit) {
	composable(Route.HOME) {
		HomeScreen(scanManager = scanManager, onSearchClick = onSearchClick)
	}
}

private fun isRouteInEntry(route: String, entry: NavBackStackEntry?): Boolean =
	entry?.destination?.hierarchy?.any { it.route?.startsWith(route) ?: false } == true

private fun getCurrentRoute(entry: NavBackStackEntry?): String? =
	entry?.destination?.hierarchy?.firstOrNull()?.route

fun getAppTopBarTitle(route: String): String {
	return when (route) {
		Route.LOGIN -> "lOGIN"
		Route.HOME -> "HOME"
		Route.PICK_DETAILS,
		Route.PICK -> "PICK"
		Route.PACK_ORDER, Route.STAGE_ORDER,
		Route.PREP -> "PREP"
		Route.ORDERS -> "ORDERS"
		Route.ORDERS_DETAILS -> "ORDER DETAILS"
		Route.ORDERS_PICKUP -> "PICK UP"
		Route.SETTINGS -> "SETTINGS"
		else -> ""
	}
}
