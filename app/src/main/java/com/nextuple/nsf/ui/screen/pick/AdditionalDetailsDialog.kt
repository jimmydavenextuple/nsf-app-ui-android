package com.nextuple.nsf.ui.screen.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.AttributeText
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun AdditionalDetailsDialog(onHandQty: String, lastReceived: String, lastReturned: String) {
	Dialog(onDismissRequest = {}) {
		Surface(
			shape = RoundedCornerShape(4.dp),
			color = Color.White
		) {
			Column(
				modifier = Modifier
					.padding(horizontal = 24.dp, vertical = 16.dp)
					.fillMaxWidth()
			) {
				Text(
					text = "Additional Details".uppercase(),
					fontFamily = FontFamily.ARCHIVO,
					fontSize = 16.sp,
					fontWeight = FontWeight(700),
					letterSpacing = 1.5.sp
				)
				Spacer(modifier = Modifier.size(16.dp))
				AttributeText(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 2.dp)
						.background(BrandColor.GRAY_100),
					label = stringResource(id = R.string.on_hand),
					value = onHandQty
				)
				AttributeText(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 2.dp),
					label = "Last Received",
					value = lastReceived
				)
				AttributeText(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 2.dp)
						.background(BrandColor.GRAY_100),
					label = "Last Returned",
					value = lastReturned
				)
			}
		}
	}
}

@Composable
@Preview
private fun OnHandDialogPreview() {
	AdditionalDetailsDialog(onHandQty = "6", lastReceived = "02/01/2023", lastReturned = "08/21/2023")
}
