package com.nextuple.nsf.ui.screen.prep

import androidx.compose.ui.test.junit4.createComposeRule
import com.nextuple.nsf.ui.state.InfoViewModel
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class PrepScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun prepScreenWithoutPrepTasks() {
		renderPrepScreen()

		compareScreenshot(composeTestRule)
	}

	@Test
	fun prepScreenWithPrepTasks() {
		renderPrepScreen()

		compareScreenshot(composeTestRule)
	}

	private fun renderPrepScreen() {
		composeTestRule.setContent {
			PrepScreen(
				currentPrepStage = InfoViewModel.PrepStage.Landing,
				navigateToPrepOrder = {},
				numPackTasks = 0,
				scanManager = NoOpScanManager(),
				onScanGear = {},
				onClickPackByOrder = {}
			)
		}
	}
}
