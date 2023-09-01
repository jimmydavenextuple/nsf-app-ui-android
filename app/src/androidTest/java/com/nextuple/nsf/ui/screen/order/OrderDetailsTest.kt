package com.nextuple.nsf.ui.screen.order

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.karumi.shot.ScreenshotTest
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

	private fun renderOrderDetails() {
		composeTestRule.setContent {
			OrderDetails(
				onBackButtonClick = {},
				onStartPickup = {},
				onPickupExtend = {},
				onPickupRemoveCheckIn = {},
				onStartPickupCompletion = {}
			)
		}
	}

	private fun renderOrderDetailsOnExpand() {
		composeTestRule.apply {
			setContent {
				OrderDetails(
					onBackButtonClick = {},
					onStartPickup = {},
					onPickupExtend = {},
					onPickupRemoveCheckIn = {},
					onStartPickupCompletion = {}
				)
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
				OrderDetails(
					onBackButtonClick = {},
					onStartPickup = {},
					onPickupExtend = {},
					onPickupRemoveCheckIn = {},
					onStartPickupCompletion = {}
				)
			}
			onNodeWithTag("showHideBtn")
				.assertIsEnabled()
				.performClick()

			onNodeWithText("PRINT HOLD SLIP").performScrollTo()
		}
	}
}
