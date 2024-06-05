package com.nextuple.nsf.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.SecondaryButton
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun PackDeclineModal(
	declineReasonsList: List<String>,
	onDismissRequest: () -> Unit,
	onConfirmDecline: (declineReason: String) -> Unit
) {
	val (selectedOption, onOptionSelected) = remember { mutableStateOf(declineReasonsList[0]) }

	Card(
		modifier = Modifier
			.wrapContentSize()
			.background(BrandColor.GRAY_50)
	) {
		Dialog(
			onDismissRequest = { onDismissRequest() },
			properties = DialogProperties(
				dismissOnClickOutside = true
			)
		) {
			Surface {
				Column {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 10.dp, horizontal = 20.dp),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = stringResource(R.string.decline_reason),
							style = TextStyle(
								fontSize = 20.sp,
								fontFamily = FontFamily.ARCHIVO,
								fontWeight = FontWeight.SemiBold,
								color = BrandColor.BLACK
							)
						)
						Icon(
							modifier = Modifier
								.align(Alignment.CenterVertically)
								.semantics { testTag = "InfoModalCloseButton" }
								.clickable { onDismissRequest() },
							imageVector = ImageVector.vectorResource(R.drawable.ic_close),
							tint = BrandColor.GRAY_900,
							contentDescription = "close"
						)
					}
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 10.dp, horizontal = 20.dp),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = stringResource(R.string.decline_modal_description),
							style = TextStyle(
								fontSize = 14.sp,
								fontFamily = FontFamily.ARCHIVO,
								color = BrandColor.BLACK
							)
						)
					}
					declineReasonsList.forEach {
						RadioButtonRow(it, selectedOption, onOptionSelected)
					}
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(top = 20.dp, bottom = 6.dp, start = 10.dp, end = 10.dp),
						horizontalArrangement = Arrangement.Center
					) {
						PrimaryButton(
							modifier = Modifier
								.fillMaxWidth(0.75f)
								.semantics { testTag = "InfoModalButton" },
							text = stringResource(R.string.confirm_decline),
							buttonColor = BrandColor.PINK_NT,
							onButtonClick = {
								when (selectedOption) {
									"Damaged" -> onConfirmDecline("DAMAGE")
									"Not Found" -> onConfirmDecline("NOT_FOUND")
								}
								onDismissRequest()
							},
							contentColor = BrandColor.GRAY_100
						)
					}
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(top = 6.dp, bottom = 20.dp, start = 10.dp, end = 10.dp),
						horizontalArrangement = Arrangement.Center
					) {
						SecondaryButton(
							modifier = Modifier
								.fillMaxWidth(0.75f)
								.semantics { testTag = "InfoModalButton" },
							text = stringResource(R.string.back),
							onButtonClick = {
								onDismissRequest()
							}
						)
					}
				}
			}
		}
	}
}

@Composable
private fun RadioButtonRow(
	declineReason: String,
	selectedOption: String,
	onOptionSelected: (declineReason: String) -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center
	) {
		RadioButton(
			selected = (declineReason == selectedOption),
			onClick = { onOptionSelected(declineReason) },
			colors = RadioButtonDefaults.colors(
				selectedColor = BrandColor.BLACK,
				unselectedColor = BrandColor.BLACK
			)
		)
		Text(
			modifier = Modifier.width(100.dp),
			text = declineReason,
			style = TextStyle(
				fontSize = 14.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight(400),
				color = BrandColor.BLACK
			)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun DeclineModalPreview() {
	PackDeclineModal(
		declineReasonsList = listOf("Damaged", "Not Found"),
		onDismissRequest = {},
		onConfirmDecline = {}
	)
}
