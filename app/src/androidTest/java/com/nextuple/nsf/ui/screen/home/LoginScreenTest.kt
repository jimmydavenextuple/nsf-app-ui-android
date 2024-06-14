package com.nextuple.nsf.ui.screen.home

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.ui.util.NoOpScanManager
import org.junit.Rule
import org.junit.Test

class LoginScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun loginScreenDefault() {
		renderLoginScreen(false)

		compareScreenshot(composeTestRule)
	}

	@Test
	fun loginScreenErrorInvalidUserId() {
		renderLoginScreen(true, "Invalid User Id")

		compareScreenshot(composeTestRule)
	}

	private fun renderLoginScreen(isValid: Boolean, errorMessage: String? = null) {
		composeTestRule.setContent {
			LoginScreen(
				isFormInvalid = isValid,
				resetIsFormInvalid = {},
				errorMessage = errorMessage,
				showProgressBar = false,
				onSubmit = { _, _ -> },
				isLoggedIn = false,
				onLoggedIn = {},
				scanManager = NoOpScanManager(),
				haptics = null,
				isInValidSymbology = { _ -> false }
			)
		}
	}
}
