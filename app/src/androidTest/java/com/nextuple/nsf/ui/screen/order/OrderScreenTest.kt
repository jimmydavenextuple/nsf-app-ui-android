package com.nextuple.nsf.ui.screen.order

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.util.TestData
import org.junit.Rule
import org.junit.Test

class OrderScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun orderScreenDefault() {
		renderOrderScreen()

		compareScreenshot(composeTestRule)
	}

	private fun renderOrderScreen() {
		composeTestRule.setContent {
			OrderScreen(
				scanManager = NoOpScanManager(),
				getOrders = { _, _ -> },
				getOrderDetails = {},
				allOrderList = TestData.SAMPLE_ORDERS_RESPONSE,
				pickupOrderList = TestData.SAMPLE_ORDERS_RESPONSE,
				getOrderDetailsCompletion = {},
				homeSearchInput = ""
			)
		}
	}
}
