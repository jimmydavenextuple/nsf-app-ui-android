package com.nextuple.nsf.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.ui.common.callToAction.CallToAction
import com.nextuple.nsf.ui.common.callToAction.CallToActionMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CallToActionTest : ScreenshotTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() {
	}

	@Test
	fun should_display_floating_action_button() {
		val testModifier = Modifier

		composeTestRule.apply {
			setContent {
				CallToAction(
					modifier = testModifier,
					callToActionMode = CallToActionMode.Loading()
				)
			}

			onNodeWithTag("callToAction")
				.assertIsDisplayed()
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_execute_onClick_when_CTA_is_clicked() {
		val testModifier = Modifier
		var clicked = false
		val onClick = { clicked = true }

		composeTestRule.apply {
			setContent {
				CallToAction(
					modifier = testModifier,
					callToActionMode = CallToActionMode.Scan(),
					onClick = onClick
				)
			}

			onNodeWithTag("callToAction")
				.performClick()

			runBlocking { delay(500) }
		}

		assert(clicked)
		compareScreenshot(composeTestRule)
	}
}
