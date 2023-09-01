package com.nextuple.nsf.ui.screen.home

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
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
	fun loginScreenErrorInvalidDKS() {
		renderLoginScreen(true, "Invalid DKS number.")

		compareScreenshot(composeTestRule)
	}

	private fun renderLoginScreen(isValid: Boolean, errorMessage: String? = null) {
		composeTestRule.setContent {
			LoginScreen(
				isInvalid = isValid,
				resetIsInvalid = {},
				errorMessage = errorMessage,
				showProgressBar = false,
				onSubmitDks = {},
				isLoggedIn = false,
				onLoggedIn = {
				}
			)
		}
	}
}
