package com.nextuple.nsf.ui.screen.prep

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.ui.state.AppViewModel
import com.nextuple.nsf.util.TestData
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
		renderPrepScreen(prepTasks = TestData.PREP_TASK_LIST)

		compareScreenshot(composeTestRule)
	}

	private fun renderPrepScreen(prepTasks: List<StoreOverviewResponse.PrepTask>? = null) {
		composeTestRule.setContent {
			PrepScreen(
				currentPrepStage = AppViewModel.PrepStage.Landing,
				navigateToStage1 = {},
				navigateToStage2 = {},
				onStartPack = { },
				prepTasks = prepTasks
			)
		}
	}
}
