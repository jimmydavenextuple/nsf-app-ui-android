//package com.nextuple.nsf.ui.common
//
//import androidx.activity.ComponentActivity
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.assertIsEnabled
//import androidx.compose.ui.test.assertIsNotEnabled
//import androidx.compose.ui.test.assertTextEquals
//import androidx.compose.ui.test.hasSetTextAction
//import androidx.compose.ui.test.hasTestTag
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.compose.ui.test.performTextInput
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.nextuple.nsf.R
//import com.nextuple.nsf.ui.component.PrinterModal
//import com.nextuple.nsf.ui.util.GenericViewState
//import org.junit.Assert.assertTrue
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class PrinterModalTest {
//
//	@get:Rule
//	val composeTestRule = createAndroidComposeRule<ComponentActivity>()
//
//	@Test
//	fun should_show_modal() {
//		composeTestRule.apply {
//			setContent {
//				BuildPrinterModal()
//			}
//
//			val modalTitle = activity.getString(R.string.connect_printer)
//			val modalSubTitle = activity.getString(R.string.connect_printer_subtitle)
//			onNodeWithText(modalTitle).assertExists()
//			onNodeWithText(modalSubTitle).assertExists()
//		}
//	}
//
//	@Test
//	fun should_enable_connect_button_when_ip_text_is_valid() {
//		val textField = hasTestTag("TopLabeledTextFieldFieldValue") and hasSetTextAction()
//		val connectButton = hasTestTag("InfoModalButton")
//
//		composeTestRule.apply {
//			setContent {
//				BuildPrinterModal()
//			}
//
//			onNode(textField).assertIsDisplayed().assertTextEquals("")
//			onNode(connectButton).assertIsNotEnabled()
//			onNode(textField).performTextInput("123")
//			onNode(connectButton).assertIsEnabled()
//		}
//	}
//
//	@Test
//	fun should_not_enable_connect_button_when_ip_text_is_empty() {
//		val textField = hasTestTag("TopLabeledTextFieldFieldValue") and hasSetTextAction()
//		val connectButton = hasTestTag("InfoModalButton")
//
//		composeTestRule.apply {
//			setContent {
//				BuildPrinterModal()
//			}
//
//			onNode(textField).assertIsDisplayed().assertTextEquals("")
//			onNode(connectButton).assertIsNotEnabled()
//		}
//	}
//
//	@Test
//	fun should_call_onConnectPrinter_when_connect_button_is_clicked() {
//		var didCallAction = false
//		val textField = hasTestTag("TopLabeledTextFieldFieldValue") and hasSetTextAction()
//		val connectButton = hasTestTag("InfoModalButton")
//
//		composeTestRule.apply {
//			setContent {
//				BuildPrinterModal(
//					onConnectPrinter = { _, _ ->
//						didCallAction = true
//					}
//				)
//			}
//
//			onNode(textField).assertIsDisplayed().assertTextEquals("")
//			onNode(connectButton).assertIsNotEnabled()
//			onNode(textField).performTextInput("123")
//			onNode(connectButton).assertIsEnabled()
//			onNode(connectButton).performClick()
//			assertTrue(didCallAction)
//		}
//	}
//
//	@Composable
//	private fun BuildPrinterModal(
//		toggleModal: (Boolean) -> Unit = {},
//		onConnectPrinter: (Printer, String) -> Unit = { _, _ -> }
//	) {
//		PrinterModal(
//			ipPrefix = "1.1.1.",
//			printer = Printer(
//				printerName = PrinterName.BOPIS.name,
//				ipAddress = "",
//				connectionStatus = false
//			),
//			onReset = { },
//			printerConnectionState = GenericViewState.Idle,
//			toggleModal = toggleModal,
//			onConnectPrinter = onConnectPrinter
//		)
//	}
//}
