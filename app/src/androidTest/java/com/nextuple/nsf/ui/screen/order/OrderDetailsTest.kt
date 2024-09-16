package com.nextuple.nsf.ui.screen.order

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.retrofit.dto.response.AthleteCheckInDetail
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.ui.state.CancelReasonData
import com.nextuple.nsf.ui.util.Printer
import org.junit.Rule
import org.junit.Test

class OrderDetailsTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun orderDetailsDefault() {
		renderOrderDetails()

		compareScreenshot(composeTestRule)
	}

	@Test
	fun orderDetailsOnExpand() {
		renderOrderDetailsOnExpand()

		compareScreenshot(composeTestRule)
	}

	@Test
	fun orderDetailsOnExpandScrollToBottom() {
		renderOrderDetailsOnExpandScrollToBottom()

		compareScreenshot(composeTestRule)
	}

	@Test
	fun shouldShowAgedCancelCardWhenOrderStatusIsAged() {
		composeTestRule.apply {
			setContent {
				BaseOrderDetailScreen(orderStatusText = "Aged")
			}
			onNodeWithTag("AgedCancelCard").assertIsDisplayed()
		}
	}

	@Test
	fun shouldNotShowAgedCancelCardWhenOrderStatusIsNotAged() {
		composeTestRule.apply {
			setContent {
				BaseOrderDetailScreen(orderStatusText = "Ready")
			}
			onNodeWithTag("AgedCancelCard").assertDoesNotExist()
		}
	}

	@Test
	fun shouldShowCancelOrderButtonWhenOrderStatusIsNotAged() {
		composeTestRule.apply {
			setContent {
				BaseOrderDetailScreen(orderStatusText = "Ready")
			}
			onNodeWithText("CANCEL ORDER").assertIsEnabled()
				.assertHasClickAction()
		}
	}

	@Test
	fun shouldNotShowCancelOrderButtonWhenOrderStatusIsAged() {
		composeTestRule.apply {
			setContent {
				BaseOrderDetailScreen(orderStatusText = "Aged")
			}
			onNodeWithText("CANCEL ORDER").assertDoesNotExist()
		}
	}

	@Test
	fun shouldEnableExtendPickupButtonWhenOrderIsNotExtended() {
		composeTestRule.apply {
			setContent {
				BaseOrderDetailScreen(
					pickupTaskId = null,
					pickupExtendedCount = null
				)
			}
			onNodeWithText("EXTEND PICKUP").assertIsEnabled()
		}
	}

	@Test
	fun shouldDisableExtendPickupButtonWhenOrderHasBeenExtended() {
		composeTestRule.apply {
			setContent {
				BaseOrderDetailScreen(
					pickupTaskId = 1234L,
					pickupExtendedCount = 1
				)
			}
			onNodeWithText("EXTEND PICKUP").assertIsNotEnabled()
		}
	}

	private fun renderOrderDetails() {
		composeTestRule.setContent {
			EmptyOrderDetailsScreen()
		}
	}

	private fun renderOrderDetailsOnExpand() {
		composeTestRule.apply {
			setContent {
				EmptyOrderDetailsScreen()
			}
			onNodeWithTag("showHideBtn")
				.assertIsEnabled()
				.performClick()

			onNodeWithText("Pickup Actions").performScrollTo()
		}
	}

	private fun renderOrderDetailsOnExpandScrollToBottom() {
		composeTestRule.apply {
			setContent {
				EmptyOrderDetailsScreen()
			}
			onNodeWithTag("showHideBtn")
				.assertIsEnabled()
				.performClick()

			onNodeWithText("PRINT HOLD SLIP").performScrollTo()
		}
	}

	@Composable
	private fun BaseOrderDetailScreen(
		orderStatusText: String? = null,
		pickupTaskId: Long? = null,
		pickupExtendedCount: Int? = null
	) {
		OrderDetailsScreen(
			orderDetailsResponse = OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = ""
				),
				orderStatusText = orderStatusText,
				pickupExtendedCount = pickupExtendedCount,
				athleteCheckInDetail = AthleteCheckInDetail(
					pickupTaskId = pickupTaskId
				)
			),
			athleteName = "",
			athleteProxyName = "",
			athletePhoneNumber = "",
			orderNumber = "",
			orderDate = Pair("", ""),
			expectedDate = "",
			receivedDate = "",
			packedOnDate = Pair("", ""),
			pickedUpOnDate = Pair("", ""),
			holdingLocation = "",
			declineModalOptions = emptyList(),
			onStartPickup = {},
			onPickupExtend = {},
			onPickupRemoveCheckIn = {},
			onStartPickupCompletion = {},
			onPrintHoldSlip = {},
			ipPrefix = "",
			printer = Printer(
				printerName = "BOPIS",
				ipAddress = "",
				connectionStatus = false
			),
			onConnectPrinter = { _, _ -> },
			onResetPrinter = {},
			onCancelOrder = { _, _, _ -> },
			cancelReasonData = CancelReasonData(),
			onCancelComplete = {},
			onCancelAgedError = {},
			holdingAreas = emptyList(),
			resetScanLocationState = {},
			onLocationChange = { _, _ -> },
			scanManager = null,
			onPackOrder = {},
			bypassPrinter = false,
			holdSlipZpl = mutableListOf(),
			resetGetHoldSlipState = {}
		)
	}

	@Composable
	private fun EmptyOrderDetailsScreen() {
		OrderDetailsScreen(
			athleteName = "",
			athleteProxyName = "",
			athletePhoneNumber = "",
			orderNumber = "",
			orderDate = Pair("", ""),
			expectedDate = "",
			receivedDate = "",
			packedOnDate = Pair("", ""),
			pickedUpOnDate = Pair("", ""),
			holdingLocation = "",
			declineModalOptions = emptyList(),
			onStartPickup = {},
			onPickupExtend = {},
			onPickupRemoveCheckIn = {},
			onStartPickupCompletion = {},
			onPrintHoldSlip = {},
			ipPrefix = "",
			printer = Printer(printerName = "BOPIS", ipAddress = "", connectionStatus = false),
			onConnectPrinter = { _, _ -> },
			onResetPrinter = {},
			onCancelOrder = { _, _, _ -> },
			cancelReasonData = CancelReasonData(),
			onCancelComplete = {},
			onCancelAgedError = {},
			holdingAreas = emptyList(),
			resetScanLocationState = {},
			onLocationChange = { _, _ -> },
			scanManager = null,
			onPackOrder = {},
			bypassPrinter = false,
			holdSlipZpl = mutableListOf(),
			resetGetHoldSlipState = {}
		)
	}
}
