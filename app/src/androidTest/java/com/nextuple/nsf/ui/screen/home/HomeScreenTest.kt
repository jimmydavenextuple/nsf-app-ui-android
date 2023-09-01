package com.nextuple.nsf.ui.screen.home

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.ui.util.NoOpScanManager
import org.junit.Rule
import org.junit.Test

class HomeScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun homeScreenDefault() {
		renderHomeScreen()

		compareScreenshot(composeTestRule)
	}

	private fun renderHomeScreen() {
		composeTestRule.setContent {
			HomeScreen(NoOpScanManager(), onSearchClick = {})
		}
	}
}
