package com.nextuple.nsf.ui.screen.pick

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nextuple.nsf.R.drawable
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun LocationDialog(
	locations: List<String>
) {
	Dialog(onDismissRequest = {}) {
		Surface(
			shape = RoundedCornerShape(4.dp),
			color = Color.White
		) {
			Column(
				modifier = Modifier
					.padding(20.dp)
					.fillMaxWidth()
			) {
				Row {
					Image(
						modifier = Modifier
							.padding(end = 4.dp)
							.align(Alignment.CenterVertically),
						imageVector = ImageVector.vectorResource(drawable.ic_location),
						contentDescription = "Locations Icon"
					)

					Text(
						text = "locations".uppercase(),
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 16.sp,
						fontWeight = FontWeight(700),
						letterSpacing = 1.5.sp
					)
				}

				locations.forEach {
					Text(
						modifier = Modifier.align(Alignment.CenterHorizontally),
						text = it,
						fontSize = 16.sp,
						lineHeight = 20.8.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(400),
						textAlign = TextAlign.Center,
						letterSpacing = 0.5.sp
					)
				}
			}
		}
	}
}

@Composable
@Preview
private fun LocationDialogPreview() {
	LocationDialog(locations = listOf("F1.S1.04A", "F1.S1.04B", "F1.S1.04C", "F1.S1.04D"))
}
