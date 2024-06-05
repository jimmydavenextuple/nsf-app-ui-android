package com.nextuple.nsf.ui.screen.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.AttributeText
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun AdditionalDetailsDialog(
	onHandQty: String,
	lastReceived: String,
	lastReturned: String,
	onDismissRequest: () -> Unit
) {
	Dialog(
		onDismissRequest = { onDismissRequest() },
		properties = DialogProperties(
			dismissOnBackPress = true,
			dismissOnClickOutside = true
		)
	) {
		Surface(
			color = Color.White
		) {
			Column(
				modifier = Modifier
					.padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
					.fillMaxWidth()
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = stringResource(R.string.additional_details),
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 18.sp,
						fontWeight = FontWeight(700),
						letterSpacing = 0.5.sp
					)
					Icon(
						modifier = Modifier
							.align(Alignment.CenterVertically)
							.size(24.dp)
							.semantics { testTag = "InfoModalCloseButton" }
							.clickable { onDismissRequest() },
						imageVector = ImageVector.vectorResource(R.drawable.ic_close),
						tint = BrandColor.GRAY_900,
						contentDescription = "close"
					)
				}
				Spacer(modifier = Modifier.size(12.dp))
				AttributeText(
					modifier = Modifier
						.fillMaxWidth()
						.background(BrandColor.GRAY_100)
						.padding(horizontal = 2.dp, vertical = 4.dp),
					label = stringResource(id = R.string.on_hand),
					value = onHandQty,
					fontSize = 12.sp
				)
				AttributeText(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 2.dp, vertical = 4.dp),
					label = stringResource(R.string.last_received),
					value = lastReceived,
					fontSize = 12.sp
				)
				AttributeText(
					modifier = Modifier
						.fillMaxWidth()
						.background(BrandColor.GRAY_100)
						.padding(horizontal = 2.dp, vertical = 4.dp),
					label = stringResource(R.string.last_returned),
					value = lastReturned,
					fontSize = 12.sp
				)
			}
		}
	}
}

@Composable
@Preview
private fun OnHandDialogPreview() {
	AdditionalDetailsDialog(
		onHandQty = "6",
		lastReceived = "02/01/2023",
		lastReturned = "08/21/2023"
	) {}
}
