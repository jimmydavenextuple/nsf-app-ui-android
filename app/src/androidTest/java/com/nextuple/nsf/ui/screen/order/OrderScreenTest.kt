package com.nextuple.nsf.ui.screen.order

import androidx.compose.ui.test.junit4.createComposeRule
import com.nextuple.nsf.ui.screen.order.OrderScreenTab.READY
import com.nextuple.nsf.util.TestData
import com.karumi.shot.ScreenshotTest
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
				getOrders = { _ -> },
				getOrderDetails = {},
				getOrderDetailsCompletion = {},
				readyOrders = TestData.SAMPLE_ORDERS_RESPONSE,
				inProgressOrders = TestData.SAMPLE_ORDERS_RESPONSE,
				orderTypeFilters = emptyList(),
				orderStatusFilters = emptyList(),
				selectedTab = READY,
				onSelectTab = {},
				onApplyFilters = { _, _ -> }
			)
		}
	}
}
