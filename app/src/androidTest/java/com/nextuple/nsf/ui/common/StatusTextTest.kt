package com.nextuple.nsf.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nextuple.nsf.ui.common.chip.OrderStatusChip
import com.nextuple.nsf.ui.theme.BrandColor
import com.karumi.shot.ScreenshotTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatusTextTest : ScreenshotTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() { }

	@Test
	fun should_display_text_status() {
		val status = "Order Ready"
		val backgroundColor = BrandColor.GREEN_500
		val testModifier = Modifier

		composeTestRule.apply {
			setContent {
				OrderStatusChip(
					modifier = testModifier,
					statusText = status,
					backgroundColor = backgroundColor
				)
			}

			onNodeWithTag("StatusText")
				.assertIsDisplayed()
				.assertTextEquals(status)
		}
		compareScreenshot(composeTestRule)
	}
}
