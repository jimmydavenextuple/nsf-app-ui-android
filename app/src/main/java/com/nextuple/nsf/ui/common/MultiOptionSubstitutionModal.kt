package com.nextuple.nsf.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.PickTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun MultiOptionSubstitutionModal(
	title: String?,
	subTitle: String?,
	buttons: List<PickTaskItem>,
	buttonClick: (String) -> Unit,
	crossIconClick: () -> Unit,
	backgroundColor: Color = Color.White,
	shape: Shape = RoundedCornerShape(4.dp),
	dismissOnBackPress: Boolean = true,
	dismissOnClickOutside: Boolean = true,
	onDismissRequest: () -> Unit
) {
	Dialog(
		onDismissRequest = {
			onDismissRequest()
		},
		properties = DialogProperties(
			dismissOnBackPress = dismissOnBackPress,
			dismissOnClickOutside = dismissOnClickOutside
		)
	) {
		Surface(
			modifier = Modifier.verticalScroll(rememberScrollState()),
			shape = shape,
			color = backgroundColor
		) {
			Column(
				modifier = Modifier
					.wrapContentHeight(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Row(
					modifier = Modifier
						.padding(20.dp)
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					if (title != null) {
						Text(
							text = title,
							style = TextStyle(
								fontSize = 20.sp,
								fontWeight = FontWeight.Bold,
								letterSpacing = 0.5.sp
							)
						)
						Icon(
							modifier = Modifier
								.align(Alignment.CenterVertically)
								.clickable { crossIconClick.invoke() },
							imageVector = ImageVector.vectorResource(R.drawable.ic_close),
							tint = BrandColor.GRAY_900,
							contentDescription = "close"
						)
					}
				}
				if (subTitle != null) {
					Text(
						modifier = Modifier
							.padding(20.dp, 0.dp, 20.dp, 20.dp),
						text = subTitle,
						style = TextStyle(
							fontSize = 16.sp,
							letterSpacing = 0.5.sp,
							fontWeight = FontWeight.Normal
						)
					)
				}
				if (buttons.isNotEmpty()) {
					for (item in buttons) {
						HorizontalDivider(
							color = BrandColor.DARK_BLUE,
							thickness = 1.dp
						)
						SubstitutionItemCard(
							Modifier
								.padding(20.dp, 16.dp, 20.dp, 16.dp)
								.fillMaxWidth()
								.clickable { buttonClick(item.sku) },
							substitutionItem = item
						)
					}
				}
			}
		}
	}
}

@Composable
private fun SubstitutionItemCard(
	modifier: Modifier = Modifier,
	substitutionItem: PickTaskItem
) {
	Row(
		modifier = modifier
	) {
		AsyncImage(
			model = substitutionItem.productImageUrls.firstOrNull(),
			error = painterResource(id = R.drawable.placeholder_image),
			placeholder = painterResource(id = R.drawable.placeholder_image),
			contentDescription = null,
			modifier = Modifier.size(30.dp)
		)
		Column(
			modifier = Modifier
				.align(Alignment.CenterVertically)
				.padding(10.dp, 0.dp, 0.dp, 0.dp)
		) {
			Text(
				text = substitutionItem.productName,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				fontSize = 14.sp,
				fontWeight = FontWeight.Bold,
				letterSpacing = 0.5.sp
			)
		}
	}
}

@Composable
@Preview
fun MultiOptionSubstitutionModalPreview() {
	MultiOptionSubstitutionModal(
		title = "Unit Locations",
		subTitle = "This product has multiple locations assigned to it. Where did you pick this unit it from?",
		buttons = listOf(
			PickTaskItem(
				sku = "2345",
				productBrand = "BOMBAS",
				productName = "Hoka Women’s Clifton 9 Running Shoes",
				productImageUrls = listOf(
					"https://picsum.photos/1705",
					"https://picsum.photos/1726",
					"https://picsum.photos/1701"
				),
				productHighResImageUrls = listOf(
					"https://picsum.photos/1705",
					"https://picsum.photos/1726",
					"https://picsum.photos/1701"
				),
				locations = listOf("F1.S1.04A"),
				primaryAttr = ProductAttribute(name = "Color", value = "Cyclamen"),
				secondaryAttr = ProductAttribute(name = "Size", value = "7.5"),
				tertiaryAttr = ProductAttribute(name = "Style", value = "12345"),
				onHandQty = 10,
				upcs = listOf("4002560185162"),
				qty = 1,
				declinedQty = 0,
				pickedQty = 0,
				clearanceColorRgb = "0,175,65",
				clearanceColorDesc = "GREEN",
				style = null
			)
		),
		buttonClick = { },
		crossIconClick = { },
		onDismissRequest = { }
	)
}
