package com.nextuple.nsf.ui.screen.order

import androidx.compose.ui.test.junit4.createComposeRule
import com.nextuple.nsf.retrofit.dto.response.AthleteCheckInDetail
import com.nextuple.nsf.retrofit.dto.response.AthleteDetail
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.ui.state.Printer
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.karumi.shot.ScreenshotTest
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
			OrderPickupScreen(
				scanManager = NoOpScanManager(),
				orderNumber = "123456",
				athleteDetail = AthleteDetail(
					athleteFirstName = "Anna",
					athleteLastName = "Heisey",
					athleteProxyFirstName = "Eli",
					athleteProxyLastName = "Heisey",
					athletePhoneNumber = "5087695491"
				),
				checkInDetail = AthleteCheckInDetail(
					athleteLocation = "Curbside Spot #2"
				),
				frDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "123456.001"
				),
				defaultStep = defaultStep,
				onOrderPickupClicked = {},
				onOrderPickupSuccess = {},
				scanHoldSlipState = GenericViewState.Loading,
				onResetHoldSlipScan = {},
				onScanSuccess = { _ -> },
				ipPrefix = "",
				printer = Printer(printerName = "BOPIS", ipAddress = "", connectionStatus = false),
				onPrintHoldSlip = {},
				onPrintHoldSlipSuccessCallBack = {},
				onResetPrintHoldSlip = {},
				onConnectPrinter = { _, _ -> },
				onResetPrinter = {}
			)
		}
	}
}
