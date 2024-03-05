package com.nextuple.nsf.ui.screen.pick

import androidx.compose.ui.test.junit4.createComposeRule
import com.nextuple.nsf.ui.util.GenericViewState
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class PickScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun pickScreenDefault() {
		renderPickScreen()

		compareScreenshot(composeTestRule)
	}

	private fun renderPickScreen() {
		composeTestRule.setContent {
			PickScreen(
				unitsWorked = 1,
				totalUnits = 2,
				hasActiveTask = false,
				onHasActiveTask = {},
				storeOverviewState = GenericViewState.Success
			)
		}
	}
}
