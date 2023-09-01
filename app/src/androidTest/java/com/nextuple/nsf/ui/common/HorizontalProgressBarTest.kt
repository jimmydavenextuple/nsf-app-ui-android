package com.nextuple.nsf.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karumi.shot.ScreenshotTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HorizontalProgressBarTest : ScreenshotTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() {}

	@Test
	fun should_display_progress_bar_title() {
		val title = "UNITS COMPLETED"
		val unitsCompleted = 1
		val totalUnits = 3
		val testModifier = Modifier

		composeTestRule.apply {
			setContent {
				HorizontalProgressBar(
					modifier = testModifier,
					unitsCompleted = unitsCompleted,
					totalUnits = totalUnits,
					title = title
				)
			}

			onNodeWithTag("ProgressBarTitle")
				.assertIsDisplayed()
				.assertTextEquals("$unitsCompleted/$totalUnits $title")
		}
		compareScreenshot(composeTestRule)
	}
}
