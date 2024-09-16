package com.nextuple.nsf.ui.screen.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nextuple.nsf.R
import com.nextuple.nsf.R.drawable
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun LocationDialog(
	locations: List<String>,
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
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 10.dp),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = stringResource(R.string.locations),
						fontSize = 16.sp,
						fontWeight = FontWeight(700),
						letterSpacing = 0.5.sp
					)
					Icon(
						modifier = Modifier
							.align(Alignment.CenterVertically)
							.size(24.dp)
							.semantics { testTag = "InfoModalCloseButton" }
							.clickable { onDismissRequest() },
						imageVector = ImageVector.vectorResource(drawable.ic_close),
						tint = BrandColor.GRAY_900,
						contentDescription = "close"
					)
				}

				var count = 0
				locations.forEach {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.background(if (count % 2 == 0) BrandColor.GRAY_100 else BrandColor.WHITE)
					) {
						Text(
							modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp),
							text = it,
							fontSize = 12.sp,
							lineHeight = 13.06.sp,
							fontWeight = FontWeight(400),
							textAlign = TextAlign.Center,
							letterSpacing = 0.5.sp
						)
					}
					count++
				}
			}
		}
	}
}

@Composable
@Preview
private fun LocationDialogPreview() {
	LocationDialog(
		locations = listOf("F1.S1.04A", "F1.S1.04B", "F1.S1.04C", "F1.S1.04D"),
		onDismissRequest = {}
	)
}
