package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nextuple.nsf.R
import com.nextuple.nsf.R.drawable
import com.nextuple.nsf.R.string
import com.nextuple.nsf.retrofit.dto.PackedItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.ui.common.TertiaryButton
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun OrderContentsDropdown(
	modifier: Modifier = Modifier,
	startExpanded: Boolean = false,
	isBopl: Boolean,
	packedItemList: List<PackedItem>?
) {
	var isExpanded by remember {
		mutableStateOf(startExpanded)
	}

	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(id = string.contents),
					fontSize = 18.sp,
					fontWeight = FontWeight(700),
					letterSpacing = 0.5.sp
				)
				if (!isBopl) {
					Spacer(
						modifier = Modifier
							.weight(1f)
					)
					TertiaryButton(
						text = if (isExpanded) {
							stringResource(id = string.hide)
						} else {
							stringResource(id = string.show)
						},
						onButtonClick = {
							isExpanded = !isExpanded
						},
						tag = "showHideBtn"
					)
					Icon(
						modifier = Modifier
							.padding(start = 5.dp)
							.clickable { isExpanded = !isExpanded },
						painter = if (isExpanded) {
							painterResource(id = drawable.ic_arrow_up)
						} else {
							painterResource(id = drawable.ic_arrow_drop_down)
						},
						contentDescription = "back button"
					)
				}
			}

			if (isExpanded || isBopl) {
				Spacer(modifier = Modifier.height(5.dp))
				packedItemList?.forEach {
					PackedItemCard(
						Modifier
							.padding(horizontal = 12.dp, vertical = 4.dp)
							.background(BrandColor.GRAY_100)
							.fillMaxWidth(),
						packedItem = it
					)
				}
			}
		}
	}
}

@Composable
private fun PackedItemCard(modifier: Modifier = Modifier, packedItem: PackedItem) {
	Row(
		modifier = modifier
	) {
		AsyncImage(
			model = packedItem.productImageUrls.firstOrNull(),
			error = painterResource(id = drawable.placeholder_image),
			placeholder = painterResource(id = drawable.placeholder_image),
			contentDescription = null,
			modifier = Modifier
				.padding(start = 15.dp, top = 9.dp, end = 23.dp, bottom = 15.dp)
				.size(64.dp)
		)

		Column(modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)) {
			Text(
				text = packedItem.productName,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				fontSize = 14.sp,
				fontWeight = FontWeight.Bold,
				letterSpacing = 0.5.sp
			)

			packedItem.primaryAttr?.value?.let {
				OrderTextInfo(
					label = packedItem.primaryAttr.name,
					value = it
				)
			}
			packedItem.secondaryAttr?.value?.let {
				OrderTextInfo(
					label = packedItem.secondaryAttr.name,
					value = it
				)
			}
			OrderTextInfo(
				label = stringResource(id = R.string.upc),
				value = packedItem.scannedUpc ?: ""
			)
		}
	}
}

@Composable
private fun OrderTextInfo(label: String, value: String) {
	Text(
		text = "$label: $value",
		maxLines = 1,
		overflow = TextOverflow.Ellipsis,
		fontSize = 12.sp,
		fontWeight = FontWeight.Normal,
		letterSpacing = 0.5.sp
	)
}

@Preview
@Composable
private fun PreviewOrderContentsDropdown() {
	OrderContentsDropdown(isBopl = false, packedItemList = listOf())
}

@Preview
@Composable
private fun PreviewOrderContentsDropdownBopl() {
	OrderContentsDropdown(
		isBopl = true,
		packedItemList = listOf(
			PackedItem(
				sku = "20638722",
				productName = "Wilson OPTX AVP Tour Outdoor Volleyball",
				productImageUrls = listOf("url"),
				primaryAttr = ProductAttribute(name = "Color", value = "Yellow/Black"),
				secondaryAttr = null,
				tertiaryAttr = null,
				qty = 1,
				scannedUpc = "887768901776"
			),
			PackedItem(
				sku = "23990750",
				productName = "NSF USA Hockey T-Shirt",
				productImageUrls = listOf("url"),
				primaryAttr = ProductAttribute(name = "Color", value = "Gray"),
				secondaryAttr = ProductAttribute(name = "Size", value = "M"),
				tertiaryAttr = null,
				qty = 1,
				scannedUpc = "840279623926"
			)
		)
	)
}
