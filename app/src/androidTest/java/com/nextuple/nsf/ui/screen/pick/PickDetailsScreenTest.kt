package com.nextuple.nsf.ui.screen.pick

import androidx.compose.ui.test.junit4.createComposeRule
import com.nextuple.nsf.retrofit.dto.PickTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.util.FulfillmentType.BOPIS
import com.nextuple.nsf.util.SubFulfillmentType
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class PickDetailsScreenTest : ScreenshotTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun pickDetailsScreenDefault() {
		renderPickDetailScreen()

		compareScreenshot(composeTestRule)
	}

	private fun renderPickDetailScreen() {
		composeTestRule.setContent {
			PickDetailsScreen(
				scanManager = NoOpScanManager(),
				pickDeclineState = GenericViewState.Loading,
				fulfillmentType = BOPIS,
				subFulfillmentType = SubFulfillmentType.BOPIS,
				currentPickTaskItem = PickTaskItem(
					sku = "2345",
					productBrand = "BOMBAS",
					productName = "Hoka Women’s Clifton 9 Running Shoes",
					productImageUrls = listOf(
						"https://picsum.photos/1705",
						"https://picsum.photos/1726",
						"https://picsum.photos/1701"
					),
					locations = listOf("F1.S1.04A"),
					primaryAttr = ProductAttribute(name = "Size", value = "7.5"),
					secondaryAttr = ProductAttribute(name = "Color", value = "Cyclamen"),
					tertiaryAttr = ProductAttribute(name = "Style", value = "123456"),
					onHandQty = 10,
					upcs = listOf("123456789101"),
					qty = 1,
					declinedQty = 0,
					pickedQty = 0
				),
				unitsWorked = 1,
				totalUnits = 3,
				declineModalOptions = linkedMapOf(),
				onItemPick = { _, _ -> },
				onCheckItemScan = { _, _ -> true }
			)
		}
	}
}
