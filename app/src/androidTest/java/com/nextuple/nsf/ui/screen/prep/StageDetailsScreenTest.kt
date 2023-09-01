package com.nextuple.nsf.ui.screen.prep

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.NoOpScanManager
import org.junit.Rule
import org.junit.Test

class StageDetailsScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun stageOrderDefaultWithoutProductImageList() {
		renderStageDetailsScreen()

		compareScreenshot(composeTestRule)
	}

	@Test
	fun stageOrderDefaultWithProductImageList() {
		renderStageDetailsScreen()
		compareScreenshot(composeTestRule)
	}

	private fun renderStageDetailsScreen() {
		composeTestRule.setContent {
			StageDetailsScreen(
				progressBarBackgroundColor = BrandColor.GREEN_900,
				scanManager = NoOpScanManager(),
				athlete = "Heisey, A.",
				orderNum = "000000000001",
				stageTask = null,
				onRecordHoldingLocation = { _, _ -> },
				onStageCompletionCallBack = {},
				onReprintHoldSlip = {}
			)
		}
	}
}
