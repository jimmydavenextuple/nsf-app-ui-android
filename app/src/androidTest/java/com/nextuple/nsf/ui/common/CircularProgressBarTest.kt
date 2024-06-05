package com.nextuple.nsf.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karumi.shot.ScreenshotTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CircularProgressBarTest : ScreenshotTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() {
	}

	@Test
	fun should_display_circular_progress_bar_and_text_inside() {
		val completedUnits = 3
		val totalUnits = 5
		val inProgressUnits = 1
		val centerText = "Units Worked"
		val showLegends = true

		composeTestRule.apply {
			setContent {
				CircularProgressBar(
					completedUnits = completedUnits,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits,
					centerText = centerText,
					showLegends = showLegends,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)
			}

			onNodeWithTag("CircularProgressBar")
				.assertIsDisplayed()

			onNodeWithTag("CircularProgressBarTextInside")
				.assertIsDisplayed()
				.assert(hasAnyDescendant(hasText(centerText)))
				.assert(hasAnyDescendant(hasText("$completedUnits/$totalUnits")))
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_show_centre_image_when_showCenterImage_is_true() {
		val completedUnits = 3
		val totalUnits = 5
		val inProgressUnits = 1
		val centerText = "Units Worked"
		val showLegends = true
		val showCenterImage = true

		composeTestRule.apply {
			setContent {
				CircularProgressBar(
					completedUnits = completedUnits,
					inProgressUnits = inProgressUnits,
					totalUnits = totalUnits,
					centerText = centerText,
					showLegends = showLegends,
					showCenterImage = showCenterImage,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)
			}

			onNodeWithTag("CircularProgressBarCenterImage")
				.assertIsDisplayed()
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_display_circular_progress_bar_with_completed_and_pending_units_and_text_inside() {
		val completedUnits = 3
		val totalUnits = 5
		val inProgressUnits = 0
		val centerText = "Units Worked"
		val showLegends = false

		composeTestRule.apply {
			setContent {
				CircularProgressBar(
					completedUnits = completedUnits,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits,
					centerText = centerText,
					showLegends = showLegends,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)
			}

			onNodeWithTag("CircularProgressBar")
				.assertIsDisplayed()

			onNodeWithTag("CircularProgressBarTextInside")
				.assertIsDisplayed()
				.assert(hasAnyDescendant(hasText(centerText)))
				.assert(hasAnyDescendant(hasText("$completedUnits/$totalUnits")))
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_display_circular_progress_bar_with_in_progress_and_pending_units_and_text_inside() {
		val completedUnits = 0
		val totalUnits = 5
		val inProgressUnits = 2
		val centerText = "Units Worked"
		val showLegends = false

		composeTestRule.apply {
			setContent {
				CircularProgressBar(
					completedUnits = completedUnits,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits,
					centerText = centerText,
					showLegends = showLegends,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)
			}

			onNodeWithTag("CircularProgressBar")
				.assertIsDisplayed()

			onNodeWithTag("CircularProgressBarTextInside")
				.assertIsDisplayed()
				.assert(hasAnyDescendant(hasText(centerText)))
				.assert(hasAnyDescendant(hasText("$completedUnits/$totalUnits")))
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_display_circular_progress_bar_with_completed_and_in_progress_units_and_text_inside() {
		val completedUnits = 3
		val totalUnits = 5
		val inProgressUnits = 2
		val centerText = "Units Worked"
		val showLegends = false

		composeTestRule.apply {
			setContent {
				CircularProgressBar(
					completedUnits = completedUnits,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits,
					centerText = centerText,
					showLegends = showLegends,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)
			}

			onNodeWithTag("CircularProgressBar")
				.assertIsDisplayed()

			onNodeWithTag("CircularProgressBarTextInside")
				.assertIsDisplayed()
				.assert(hasAnyDescendant(hasText(centerText)))
				.assert(hasAnyDescendant(hasText("$completedUnits/$totalUnits")))
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_display_circular_progress_bar_only_with_completed_units_and_text_inside() {
		val completedUnits = 5
		val totalUnits = 5
		val inProgressUnits = 0
		val centerText = "Units Worked"
		val showLegends = false

		composeTestRule.apply {
			setContent {
				CircularProgressBar(
					completedUnits = completedUnits,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits,
					centerText = centerText,
					showLegends = showLegends,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)
			}

			onNodeWithTag("CircularProgressBar")
				.assertIsDisplayed()

			onNodeWithTag("CircularProgressBarTextInside")
				.assertIsDisplayed()
				.assert(hasAnyDescendant(hasText(centerText)))
				.assert(hasAnyDescendant(hasText("$completedUnits/$totalUnits")))
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_display_circular_progress_bar_only_with_pending_units_and_text_inside() {
		val completedUnits = 0
		val totalUnits = 5
		val inProgressUnits = 0
		val centerText = "Units Worked"
		val showLegends = false

		composeTestRule.apply {
			setContent {
				CircularProgressBar(
					completedUnits = completedUnits,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits,
					centerText = centerText,
					showLegends = showLegends,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)
			}

			onNodeWithTag("CircularProgressBar")
				.assertIsDisplayed()

			onNodeWithTag("CircularProgressBarTextInside")
				.assertIsDisplayed()
				.assert(hasAnyDescendant(hasText(centerText)))
				.assert(hasAnyDescendant(hasText("$completedUnits/$totalUnits")))
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_display_circular_progress_bar_only_with_in_progress_units_and_text_inside() {
		val completedUnits = 0
		val totalUnits = 5
		val inProgressUnits = 5
		val centerText = "Units Worked"
		val showLegends = false

		composeTestRule.apply {
			setContent {
				CircularProgressBar(
					completedUnits = completedUnits,
					totalUnits = totalUnits,
					inProgressUnits = inProgressUnits,
					centerText = centerText,
					showLegends = showLegends,
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)
			}

			onNodeWithTag("CircularProgressBar")
				.assertIsDisplayed()

			onNodeWithTag("CircularProgressBarTextInside")
				.assertIsDisplayed()
				.assert(hasAnyDescendant(hasText(centerText)))
				.assert(hasAnyDescendant(hasText("$completedUnits/$totalUnits")))
		}
		compareScreenshot(composeTestRule)
	}
}
