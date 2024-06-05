package com.nextuple.nsf.ui.nav

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karumi.shot.ScreenshotTest
import com.nextuple.nsf.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavBarTest : ScreenshotTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() {
	}

	@Test
	fun should_display_the_items_icon() {
		val item = AppNavBarItem(
			iconResId = R.drawable.ic_prep,
			label = "test",
			route = "route",
			count = 2
		)

		composeTestRule.apply {
			setContent {
				AppNavBar(
					items = listOf(item),
					isSelected = { false },
					tasksUnassigned = listOf(0),
					onSelect = {}
				)
			}

			onNodeWithTag("navigationBarItemIcon_test", useUnmergedTree = true)
				.assertIsDisplayed()
		}
		compareScreenshot(composeTestRule)
	}

	@Test
	fun should_display_the_items_label_and_uppercase() {
		val item = AppNavBarItem(
			iconResId = R.drawable.ic_home,
			label = "test",
			route = "route",
			count = 2
		)

		composeTestRule.apply {
			setContent {
				AppNavBar(
					items = listOf(item),
					isSelected = { false },
					tasksUnassigned = listOf(0),
					onSelect = {}
				)
			}

			onNodeWithTag("navigationBarItemLabel_test", useUnmergedTree = true)
				.assertIsDisplayed()
				.assertTextEquals("TEST")
		}
		compareScreenshot(composeTestRule)
	}
}
