package com.nextuple.nsf.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun ImageModal(
	productName: String,
	brandName: String,
	imageUrls: List<String>,
	onDismissRequest: () -> Unit,
	startIndex: Int,
	changeCurrentIndex: (Int) -> Unit
) {
	Dialog(onDismissRequest = { onDismissRequest() }) {
		Surface(
			shape = RoundedCornerShape(4.dp),
			color = Color.White
		) {
			Column(
				modifier = Modifier
					.padding(20.dp)
					.fillMaxWidth()
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Column {
						Text(
							text = brandName,
							fontFamily = FontFamily.ARCHIVO,
							fontSize = 14.sp,
							fontWeight = FontWeight(700),
							letterSpacing = 1.5.sp
						)
						Text(
							text = productName,
							fontFamily = FontFamily.ARCHIVO,
							fontSize = 12.sp,
							fontWeight = FontWeight(400),
							letterSpacing = 1.5.sp
						)
					}

					Icon(
						modifier = Modifier
							.clickable { onDismissRequest() },
						imageVector = ImageVector.vectorResource(R.drawable.ic_close),
						tint = BrandColor.GRAY_900,
						contentDescription = "close"
					)
				}
				Box(
					modifier = Modifier
						.clip(RectangleShape)
						.fillMaxWidth()
				) {
					Carousel(
						modifier = Modifier
							.align(Alignment.Center)
							.padding(vertical = 10.dp),
						images = imageUrls,
						isZoomEnabled = true,
						size = DpSize(height = 264.dp, width = 264.dp),
						indexStart = startIndex,
						changeIndex = changeCurrentIndex
					)
				}
				Text(
					text = "Pinch to zoom.\n" +
						"Tap anywhere outside the modal to close.",
					fontFamily = FontFamily.ARCHIVO,
					fontSize = 10.sp,
					fontWeight = FontWeight(400),
					letterSpacing = 1.5.sp
				)
			}
		}
	}
}

@Composable
@Preview
private fun ImageDialogPreview() {
	ImageModal(
		"Product Name",
		"Nike",
		listOf(
			"https://picsum.photos/1705",
			"https://picsum.photos/1726",
			"https://picsum.photos/1701"
		),
		{},
		1,
		{ _ -> }
	)
}
