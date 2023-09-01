package com.nextuple.nsf.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karumi.shot.ScreenshotTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecondaryButtonTest : ScreenshotTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() {
	}

	@Test
	fun should_display_the_button_with_text() {
		val text = "SECONDARY"
		val testModifier = Modifier

		composeTestRule.setContent {
			SecondaryButton(
				modifier = testModifier,
				text = text
			) {}
		}

		composeTestRule.onNodeWithTag("SecondaryButton")
			.assertIsDisplayed()
			.assertTextEquals(text)
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_be_enabled_when_enabled_value_is_true_and_execute_onClick() {
		val text = "SECONDARY"
		val testModifier = Modifier
		val enabled = true
		var clicked = false
		val onButtonClick = { clicked = true }

		composeTestRule.apply {
			setContent {
				SecondaryButton(
					modifier = testModifier,
					text = text,
					enabled = enabled,
					onButtonClick = onButtonClick
				)
			}

			onNodeWithTag("SecondaryButton")
				.assertIsEnabled()
				.performClick()

			runBlocking { delay(500) }
		}

		assert(clicked)
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_be_disabled_when_enabled_value_is_false_and_will_not_execute_onClick() {
		val text = "SECONDARY"
		val testModifier = Modifier
		val enabled = false
		var clicked = false
		val onButtonClick = { clicked = true }

		composeTestRule.apply {
			setContent {
				SecondaryButton(
					modifier = testModifier,
					text = text,
					enabled = enabled,
					onButtonClick = onButtonClick
				)
			}

			onNodeWithTag("SecondaryButton")
				.assertIsNotEnabled()
				.performClick()
		}

		assert(!clicked)
		compareScreenshot(composeTestRule)
	}
}
