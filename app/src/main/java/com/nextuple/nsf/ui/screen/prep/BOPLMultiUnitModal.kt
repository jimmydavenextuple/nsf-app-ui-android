package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.ui.common.HeaderText
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.screen.prep.component.PackTaskItemCard
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.util.StringUtils

@Composable
fun BOPLMultiUnitModal(
	onDismissRequest: () -> Unit,
	packItems: List<PackTaskItem>,
	onPackItem: (String) -> Boolean = { false },
	athlete: String,
	orderNum: String,
	isStep2Active: Boolean,
	toggleStep2: () -> Unit
) {
	val athleteName = StringUtils.toLastNameFirstInitial(athlete)

	Dialog(onDismissRequest = { onDismissRequest() }) {
		Surface {
			Column(modifier = Modifier.wrapContentHeight()) {
				if (!isStep2Active) {
					Column(
						modifier = Modifier
							.heightIn(max = 495.dp)
							.verticalScroll(rememberScrollState())
					) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 10.dp, horizontal = 20.dp),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = "Scan A Unit In This Order",
								style = TextStyle(
									fontSize = 20.sp,
									fontFamily = FontFamily.ARCHIVO,
									fontWeight = FontWeight.SemiBold,
									color = BrandColor.BLACK
								)
							)
							Icon(
								modifier = Modifier
									.align(Alignment.CenterVertically)
									.semantics { testTag = "InfoModalCloseButton" }
									.clickable { onDismissRequest() },
								imageVector = ImageVector.vectorResource(R.drawable.ic_close),
								tint = BrandColor.GRAY_900,
								contentDescription = "close"
							)
						}

						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = athleteName,
								style = TextStyle(
									fontSize = 16.sp,
									lineHeight = 20.8.sp,
									fontFamily = FontFamily.ARCHIVO,
									fontWeight = FontWeight(700),
									color = BrandColor.BLACK,
									letterSpacing = 0.5.sp
								)
							)
							HeaderText(title = "Order Number", value = orderNum)
						}
						HorizontalDivider(
							modifier = Modifier.padding(horizontal = 20.dp),
							thickness = 1.dp,
							color = BrandColor.GRAY_350
						)
						Spacer(modifier = Modifier.size(10.dp))

						packItems.forEach { prepTaskItem ->
							PackTaskItemCard(
								modifier = Modifier
									.background(BrandColor.GRAY_100)
									.fillMaxWidth(.9f)
									.padding(horizontal = 5.dp)
									.align(Alignment.CenterHorizontally)
									.clickable {
										if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
											onPackItem(prepTaskItem.scannedBarcode.orEmpty())
											toggleStep2()
										}
									},
								packTaskItem = prepTaskItem,
								declineEnabled = false
							)
							Spacer(modifier = Modifier.size(5.dp))
						}
						Spacer(modifier = Modifier.size(10.dp))
					}
				} else {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 10.dp, horizontal = 20.dp),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						val packItemsCompleted =
							packItems.filter { packTaskItem -> packTaskItem.isScanned }.size
						Text(
							text = "Place Hold Slip $packItemsCompleted/${packItems.size} On Unit",
							style = TextStyle(
								fontSize = 18.sp,
								fontFamily = FontFamily.ARCHIVO,
								fontWeight = FontWeight.SemiBold,
								color = BrandColor.BLACK
							)
						)
						Icon(
							modifier = Modifier
								.align(Alignment.CenterVertically)
								.semantics { testTag = "InfoModalCloseButton" }
								.clickable { onDismissRequest() },
							imageVector = ImageVector.vectorResource(R.drawable.ic_close),
							tint = BrandColor.GRAY_900,
							contentDescription = "close"
						)
					}
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.size(189.dp)
							.padding(bottom = 10.dp),
						horizontalArrangement = Arrangement.Center
					) {
						Image(
							painter = painterResource(id = R.drawable.ic_bopl_treadmill),
							contentDescription = "Place Hold Slip"
						)
					}
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(top = 8.dp, bottom = 24.dp),
						horizontalArrangement = Arrangement.Center
					) {
						PrimaryButton(
							modifier = Modifier
								.fillMaxWidth(.6f)
								.height(40.dp),
							text = stringResource(id = R.string.ok)
						) {
							onDismissRequest()
						}
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewBoplMultiUnitModal() {
	BOPLMultiUnitModal(
		onDismissRequest = {},
		packItems = listOf(packTaskItem, packTaskItem),
		athlete = "Yuji Itadori",
		orderNum = "000000000000",
		isStep2Active = false,
		toggleStep2 = {}
	)
}

private val PACK_TASK_ITEM = PackTaskItem(
	sku = "2345",
	primaryAttr = ProductAttribute(name = "Color", value = "Cyclamen"),
	secondaryAttr = ProductAttribute(name = "Size", value = "7.5"),
	tertiaryAttr = ProductAttribute(name = "Style", value = "12345"),
	qty = 1,
	productName = "Hoka Women’s Clifton 9 Running Shoes",
	productImageUrls = listOf(
		"https://picsum.photos/1705",
		"https://picsum.photos/1726",
		"https://picsum.photos/1701"
	)
)
