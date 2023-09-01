package com.nextuple.nsf.ui.screen.prep

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.util.TestData
import org.junit.Rule
import org.junit.Test

class PackDetailsScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun packTaskScreenDefault() {
		renderPackTaskScreen()

		compareScreenshot(composeTestRule)
	}

	private fun renderPackTaskScreen() {
		composeTestRule.setContent {
			PackTaskItemCard(
				packTaskItem = TestData.PACK_TASK_ITEM
			)
		}
	}
}
