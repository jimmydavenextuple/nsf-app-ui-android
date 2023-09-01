package com.nextuple.nsf.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karumi.shot.ScreenshotTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TertiaryButtonTest : ScreenshotTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() { }

	@Test
	fun should_display_the_button_with_text() {
		val text = "TERTIARY"
		val testModifier = Modifier

		composeTestRule.apply {
			setContent {
				TertiaryButton(
					modifier = testModifier,
					text = text
				) {}
			}

			onNodeWithTag("TertiaryButton")
				.assertIsDisplayed()
				.assertTextEquals(text)
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_execute_onClick_when_button_is_clicked() {
		val testModifier = Modifier
		val text = "TERTIARY"
		var clicked = false
		val onButtonClick = { clicked = true }

		composeTestRule.apply {
			setContent {
				TertiaryButton(
					modifier = testModifier,
					text = text,
					onButtonClick = onButtonClick
				)
			}

			onNodeWithTag("TertiaryButton")
				.performClick()
		}

		assert(clicked)
		compareScreenshot(composeTestRule)
	}
}
