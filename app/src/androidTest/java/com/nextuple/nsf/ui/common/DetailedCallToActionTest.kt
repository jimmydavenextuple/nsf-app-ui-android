package com.nextuple.nsf.ui.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToAction
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailedCallToActionTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun should_display_correctly_for_scan_mode() {
		val actionText = "ACTIONTEXT"
		composeTestRule.apply {
			setContent {
				DetailedCallToAction(
					detailedCallToActionMode = DetailedCallToActionMode.Scan(actionText)
				)
			}

			onRoot(true).printToLog("detailedCallToAction")

			onNodeWithTag("detailedCallToAction").assertIsDisplayed()
			onNodeWithTag("detailedCallToAction_Scan", useUnmergedTree = true).assertIsDisplayed()
			onNodeWithText(actionText, useUnmergedTree = true).assertIsDisplayed()

			onNodeWithTag(
				"detailedCallToAction_LoadingSpinner",
				useUnmergedTree = true
			).assertDoesNotExist()
			onNodeWithTag("detailedCallToAction_Done", useUnmergedTree = true).assertDoesNotExist()
		}
	}

	@Test
	fun should_display_correctly_for_loading_mode() {
		composeTestRule.apply {
			setContent {
				DetailedCallToAction(
					detailedCallToActionMode = DetailedCallToActionMode.Loading()
				)
			}

			onNodeWithTag("detailedCallToAction").assertIsDisplayed()
			onNodeWithTag("detailedCallToAction_LoadingSpinner").assertIsDisplayed()

			onNodeWithTag("detailedCallToAction_Scan").assertDoesNotExist()
			onNodeWithTag("detailedCallToAction_Done").assertDoesNotExist()
		}
	}

	@Test
	fun should_display_correctly_for_done_mode() {
		composeTestRule.apply {
			setContent {
				DetailedCallToAction(
					detailedCallToActionMode = DetailedCallToActionMode.Done()
				)
			}

			onNodeWithTag("detailedCallToAction").assertIsDisplayed()
			onNodeWithTag("detailedCallToAction_Done", useUnmergedTree = true).assertIsDisplayed()

			onNodeWithTag("detailedCallToAction_Scan").assertDoesNotExist()
			onNodeWithTag("detailedCallToAction_LoadingSpinner").assertDoesNotExist()
		}
	}
}
