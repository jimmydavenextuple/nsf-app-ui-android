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
class TopLabeledTextFieldTest : ScreenshotTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() {
	}

	@Test
	fun should_display_label_text_and_field_text() {
		val testModifier = Modifier
		val labelText = "DKS Number"
		val fieldValue = "dks123456"

		composeTestRule.apply {
			setContent {
				TopLabeledTextField(
					modifier = testModifier,
					labelText = labelText,
					fieldValue = fieldValue,
					onValueChange = {}
				)
			}

			onNodeWithTag("TopLabeledTextFieldLabelText")
				.assertIsDisplayed()
				.assertTextEquals(labelText)

			onNodeWithTag("TopLabeledTextFieldFieldValue")
				.assertIsDisplayed()
				.assertTextEquals(fieldValue)
		}
		compareScreenshot(composeTestRule)
	}
}
