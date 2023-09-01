package com.nextuple.nsf.ui.screen.order

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import org.junit.Rule
import org.junit.Test

class OrderPickupScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun orderPickupScreenDefault() {
		renderOrderPickupScreen()

		compareScreenshot(composeTestRule)
	}

	@Test
	fun orderPickupScreenBringToAthlete() {
		renderOrderPickupScreen(STEP_BRING_TO_ATHLETE)

		compareScreenshot(composeTestRule)
	}

	private fun renderOrderPickupScreen(defaultStep: Int = STEP_GET_ORDER) {
		composeTestRule.setContent {
			PickOrderScreen(
				onBackButtonClick = {},
				orderDetails = OrderDetailsResponse(fulfillmentRequestDetail = FulfillmentRequestDetail(fulfillmentRequestNumber = "123")),
				defaultStep = defaultStep,
				onOrderPickupClicked = {},
				onOrderPickupSuccess = {}
			)
		}
	}
}
