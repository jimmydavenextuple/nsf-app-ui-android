package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun PackTaskItemCard(
	modifier: Modifier = Modifier,
	packTaskItem: PackTaskItem
) {
	Row(
		modifier = modifier
	) {
		AsyncImage(
			model = packTaskItem.productImageUrls.firstOrNull(),
			error = painterResource(id = R.drawable.placeholder_image),
			placeholder = painterResource(id = R.drawable.placeholder_image),
			contentDescription = null,
			modifier = Modifier
				.padding(horizontal = 12.dp)
				.align(Alignment.CenterVertically)
				.size(52.dp)
		)

		Column(
			modifier = Modifier
				.padding(top = 8.dp, bottom = 8.dp)
				.weight(1.0f),
			horizontalAlignment = Alignment.Start
		) {
			Text(
				modifier = Modifier.padding(bottom = 6.dp),
				text = packTaskItem.productName,
				fontSize = 14.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight.Bold,
				letterSpacing = 0.5.sp,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)

			packTaskItem.primaryAttr?.value?.let {
				TextInfo(
					label = packTaskItem.primaryAttr.name,
					value = it
				)
			}
			packTaskItem.secondaryAttr?.value?.let {
				TextInfo(
					label = packTaskItem.secondaryAttr.name,
					value = it
				)
			}
			TextInfo(
				label = stringResource(id = R.string.upc),
				value = packTaskItem.scannedBarcode ?: ""
			)
		}

		ScanIcon(
			modifier = Modifier
				.align(alignment = Alignment.CenterVertically)
				.padding(horizontal = 12.dp)
				.size(height = 30.dp, width = 34.dp),
			packTaskItem = packTaskItem
		)
	}
}

@Composable
private fun TextInfo(label: String, value: String) {
	Text(
		text = "$label: $value",
		fontSize = 12.sp,
		fontFamily = FontFamily.ARCHIVO,
		fontWeight = FontWeight.Normal,
		letterSpacing = 0.5.sp
	)
}

@Composable
private fun ScanIcon(
	modifier: Modifier = Modifier,
	packTaskItem: PackTaskItem
) {
	if (packTaskItem.isScanned) {
		Icon(
			modifier = modifier,
			imageVector = ImageVector.vectorResource(R.drawable.ic_check),
			contentDescription = "scan success icon",
			tint = BrandColor.GREEN_500
		)
	} else {
		Icon(
			modifier = modifier,
			imageVector = ImageVector.vectorResource(R.drawable.ic_scan),
			contentDescription = "scan icon",
			tint = BrandColor.ORANGE_700
		)
	}
}

@Preview(showBackground = true)
@Composable
fun PackTaskItemCardScanPreview() {
	PackTaskItemCard(
		packTaskItem = previewPackTaskItem.copy().apply {
			this.isScanned = false
		}
	)
}

@Preview(showBackground = true)
@Composable
fun PackTaskItemCardCompletePreview() {
	PackTaskItemCard(
		packTaskItem = previewPackTaskItem.copy().apply {
			this.isScanned = true
		}
	)
}

private val previewPackTaskItem = PackTaskItem(
	id = 1,
	sku = "2345",
	primaryAttr = ProductAttribute(name = "Color", value = "Cyclamen"),
	secondaryAttr = ProductAttribute(name = "Size", value = "7.5"),
	tertiaryAttr = ProductAttribute(name = "Style", value = "12345"),
	qty = 1,
	packedQty = 2,
	declinedQty = 2,
	productName = "Hoka Women’s Clifton 9 Running Shoes",
	productImageUrls = listOf(
		"https://picsum.photos/1705",
		"https://picsum.photos/1726",
		"https://picsum.photos/1701"
	)
)
