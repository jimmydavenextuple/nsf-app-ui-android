package com.nextuple.nsf.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.common.callToAction.imageResource
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun HoldSlip(
	athleteName: String?,
	lastName: String,
	packageNum: String?,
	totalPackageNum: String?,
	isSmallSize: Boolean = false,
	isScanable: Boolean,
	isSelected: Boolean,
	scanStatus: DetailedCallToActionMode
) {
	Box {
		Column(
			modifier = Modifier
				.height(if (isSmallSize && isScanable) 132.dp else 153.dp)
				.width(if (isSmallSize && isScanable) 116.dp else 136.dp)
				.background(White, shape = RoundedCornerShape(5.dp))
				.border(
					1.dp,
					if (isSelected && !isScanable) {
						SolidColor(BrandColor.GREEN_500)
					} else {
						SolidColor(
							LightGray
						)
					},
					shape = RoundedCornerShape(5.dp)
				)

		) {
			Box(
				modifier = Modifier
					.padding(
						top = 8.dp,
						start = 8.dp,
						end = 8.dp,
						bottom = if (isSmallSize && isScanable) 10.dp else 12.dp
					)
			) {
				Image(
					modifier = Modifier
						.fillMaxWidth(),
					painter = painterResource(id = R.drawable.ic_hold_slip_header),
					contentDescription = "",
					contentScale = if (isSmallSize && isScanable) ContentScale.Fit else ContentScale.Crop
				)
				if (isSelected && !isScanable) {
					Step(text = null, isActive = true, isOutlined = true, activeColor = BrandColor.GREEN_500)
				}
			}
			Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
				Text(
					modifier = Modifier,
					text = lastName,
					color = Black,
					style = TextStyle(
						fontSize = 15.sp,
						lineHeight = 20.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight.Bold
					)
				)
				if (!isSmallSize || !isScanable) {
					Text(
						text = "Purchaser: $athleteName",
						style = TextStyle(
							fontSize = 6.sp,
							lineHeight = 10.4.sp,
							fontFamily = FontFamily.ARCHIVO,
							fontWeight = FontWeight(700),
							color = BrandColor.BLACK
						)
					)
					HorizontalDivider(
						modifier = Modifier
							.fillMaxWidth(.9f)
							.padding(top = 3.dp),
						color = BrandColor.GRAY_600,
						thickness = 1.dp
					)
				}

				Image(modifier = Modifier.padding(top = 3.dp), painter = painterResource(id = R.drawable.ic_hold_slip_rect), contentDescription = "")
			}
			HorizontalDivider(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 3.dp),
				color = LightGray
			)
			Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
					if (isScanable) {
						Icon(
							modifier = Modifier
								.size(if (isSmallSize) 25.dp else 33.dp)
								.padding(bottom = 3.dp),
							imageVector = ImageVector.vectorResource(scanStatus.imageResource()),
							tint = scanStatus.contentColor,
							contentDescription = null
						)
					} else {
						Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
							Text(
								modifier = Modifier,
								text = "PART $packageNum/$packageNum",
								style = TextStyle(
									fontSize = 8.sp,
									fontFamily = FontFamily.ARCHIVO,
									fontWeight = FontWeight(700),
									color = BrandColor.GRAY_600,
									textAlign = TextAlign.Center,
									letterSpacing = 1.5.sp
								)
							)
						}
					}
				}
				if (isSmallSize && isScanable) {
					Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
						Text(
							modifier = Modifier,
							text = "PART $packageNum/$totalPackageNum",
							style = TextStyle(
								fontSize = 10.sp,
								fontFamily = FontFamily.ARCHIVO,
								fontWeight = FontWeight(700),
								color = BrandColor.GRAY_600,
								textAlign = TextAlign.Center,
								letterSpacing = 1.5.sp
							)
						)
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewHoldSlip() {
	HoldSlip(
		athleteName = "Janet Jackson",
		lastName = "Jackson",
		packageNum = "1",
		totalPackageNum = "2",
		isSmallSize = true,
		isScanable = true,
		isSelected = false,
		scanStatus = DetailedCallToActionMode.Done(backgroundColor = White, contentColor = BrandColor.BLUE_300_NT)
	)
}
