package com.nextuple.nsf.ui.screen.pick

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.ui.util.GenericViewState
import org.junit.Rule
import org.junit.Test

class PickScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun pickScreenDefault() {
		renderPickLanding()

		compareScreenshot(composeTestRule)
	}

	private fun renderPickLanding() {
		composeTestRule.setContent {
			PickLanding(
				tasksUnassigned = null,
				unitsWorked = 1,
				totalUnits = 2,
				inProgressUnits = null,
				storeOverviewState = GenericViewState.Success,
				startTaskStatus = GenericViewState.Idle,
				onStartPicking = {},
				startTaskCompletion = {},
				resetScreen = {}
			)
		}
	}
}
