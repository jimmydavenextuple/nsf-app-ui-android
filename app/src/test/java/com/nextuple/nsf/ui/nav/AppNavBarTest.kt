package com.nextuple.nsf.ui.nav

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nextuple.nsf.R
import io.mockk.justRun
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowLog

@RunWith(AndroidJUnit4::class)
class AppNavBarTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Before
	@Throws(Exception::class)
	fun setUp() {
		ShadowLog.stream = System.out
	}

	@Test
	fun `Should display the item's icon`() {
		val item = AppNavBarItem(
			iconResId = R.drawable.ic_prep,
			label = "test",
			route = "route",
			count = 2
		)

		composeRule.apply {
			setContent {
				AppNavBar(
					items = listOf(item),
					isSelected = { false },
					onSelect = {},
					tasksUnassigned = listOf(0)
				)
			}

			onNodeWithTag("navigationBarItemIcon_test", useUnmergedTree = true)
				.assertIsDisplayed()
		}
	}

	@Test
	fun `Should display the item's label and uppercase`() {
		val item = AppNavBarItem(
			iconResId = R.drawable.ic_home,
			label = "test",
			route = "route",
			count = 2
		)

		composeRule.apply {
			setContent {
				AppNavBar(
					items = listOf(item),
					isSelected = { false },
					onSelect = {},
					tasksUnassigned = listOf(0)
				)
			}

			onNodeWithTag("navigationBarItemLabel_test", useUnmergedTree = true)
				.assertIsDisplayed()
				.assertTextEquals("TEST")
		}
	}

	@Test
	fun `When a nav item is invoked, should call onSelect with the correct route`() {
		val notSelectedItem = AppNavBarItem(
			iconResId = R.drawable.ic_home,
			label = "Not_Selected_Label",
			route = "Not_Selected_Route",
			count = 2
		)
		val selectedItem = AppNavBarItem(
			iconResId = R.drawable.ic_home,
			label = "Selected_Label",
			route = "Selected_Route",
			count = 2
		)
		val items = listOf(
			notSelectedItem,
			selectedItem
		)
		val onSelect: (String) -> Unit = spyk({})
		justRun { onSelect(any()) }

		composeRule.apply {
			setContent {
				AppNavBar(
					items = items,
					isSelected = { false },
					onSelect = onSelect,
					tasksUnassigned = listOf(0)
				)
			}

			onNodeWithTag("navigationBarItem_Selected_Label")
				.performClick()

			verify { onSelect("Selected_Route") }
		}
	}
}
