package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nextuple.nsf.R
import com.nextuple.nsf.R.drawable
import com.nextuple.nsf.R.string
import com.nextuple.nsf.ui.common.InfoModal
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.SecondaryButton
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun OrderCancelModal(
	isDamaged: Boolean,
	onCancelComplete: () -> Unit,
	onDismissRequest: () -> Unit
) {
	if (isDamaged) {
		val subtitleRes = R.string.order_damaged_info
		InfoModal(
			title = stringResource(id = string.order_cancelled_title),
			subTitle = stringResource(id = subtitleRes),
			buttonText = stringResource(id = string.ok),
			buttonClick = { onCancelComplete() },
			crossIconClick = { onCancelComplete() },
			onDismissRequest = { onCancelComplete() }
		)
	} else {
		val subtitleRes = R.string.order_aged_cancelled_info
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
						Modifier
							.padding(bottom = 5.dp)
							.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(
							text = stringResource(id = string.order_aged_cancelled_title),
							style = TextStyle(
								fontSize = 20.sp,
								fontWeight = FontWeight.Bold,
								letterSpacing = 0.5.sp
							)
						)
						Icon(
							modifier = Modifier
								.align(Alignment.CenterVertically)
								.clickable { onDismissRequest() },
							imageVector = ImageVector.vectorResource(drawable.ic_close),
							tint = BrandColor.GRAY_900,
							contentDescription = "close"
						)
					}
					Text(
						text = stringResource(id = subtitleRes),
						style = TextStyle(
							fontSize = 16.sp,
							letterSpacing = 0.5.sp,
							fontWeight = FontWeight.Normal
						)
					)

					Spacer(modifier = Modifier.height(24.dp))
					Row(
						Modifier
							.padding(bottom = 5.dp)
							.fillMaxWidth(),
						horizontalArrangement = Arrangement.Center
					) {
						Image(
							imageVector = ImageVector.vectorResource(id = drawable.return_aged_to_stock),
							contentDescription = "Return Units image"
						)
					}

					PrimaryButton(
						text = CONFIRM,
						modifier = Modifier
							.fillMaxWidth()
							.padding()
					) {
						onCancelComplete()
					}
					SecondaryButton(
						text = BACK,
						modifier = Modifier
							.fillMaxWidth()
							.padding(top = 8.dp)
					) {
						onDismissRequest()
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun PreviewOrderCancelModal() {
	OrderCancelModal(
		isDamaged = false,
		onCancelComplete = {},
		onDismissRequest = {}
	)
}

@Preview
@Composable
fun PreviewOrderCancelModalDamaged() {
	OrderCancelModal(
		isDamaged = true,
		onCancelComplete = {},
		onDismissRequest = {}
	)
}
