package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.TertiaryButton
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectHoldingAreaCard(
	stepNumber: String,
	isActive: Boolean,
	isComplete: Boolean,
	holdingAreas: List<String>,
	selectedHoldingArea: String,
	isReprintActive: Boolean = false,
	isHoldingAreaTextIncluded: Boolean = true,
	onReprintHoldSlip: () -> Unit = {},
	onSelectedOptionTextChanged: (String) -> Unit = {},
	onSubmitHoldingArea: () -> Unit = {}
) {
	ExpandableStepCard(
		stepNumber = stepNumber,
		title = stringResource(id = R.string.select_holding_area),
		isActive = isActive,
		isComplete = isComplete,
		extraContent = {
			var expanded by remember { mutableStateOf(false) }
			val interactionSource = remember { MutableInteractionSource() }

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 8.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Column {
					if (isHoldingAreaTextIncluded) {
						Text(
							modifier = Modifier.padding(bottom = 4.dp),
							text = stringResource(id = R.string.holding_area_hint),
							textAlign = TextAlign.Start,
							color = BrandColor.GRAY_900,
							fontFamily = FontFamily.ARCHIVO,
							fontWeight = FontWeight.Normal,
							fontStyle = FontStyle.Normal,
							fontSize = 12.sp
						)
					}

					ExposedDropdownMenuBox(
						modifier = Modifier.size(height = 42.dp, width = 292.dp),
						expanded = expanded,
						onExpandedChange = { expanded = !expanded }
					) {
						BasicTextField(
							value = selectedHoldingArea,
							onValueChange = { onSelectedOptionTextChanged(it) },
							modifier = Modifier
								.menuAnchor()
								.fillMaxSize(),
							singleLine = true,
							readOnly = true,
							interactionSource = interactionSource,
							textStyle = TextStyle(
								fontSize = 14.sp,
								fontFamily = FontFamily.ARCHIVO,
								fontWeight = FontWeight(400),
								letterSpacing = 0.5.sp
							)
						) { innerTextField ->
							OutlinedTextFieldDefaults.DecorationBox(
								value = selectedHoldingArea,
								innerTextField = innerTextField,
								enabled = true,
								singleLine = true,
								visualTransformation = VisualTransformation.None,
								interactionSource = interactionSource,
								trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
								colors = OutlinedTextFieldDefaults.colors(
									focusedBorderColor = BrandColor.PINK_NT
								),
								contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
									start = 12.dp,
									top = 0.dp,
									end = 12.dp,
									bottom = 0.dp
								)
							)
						}

						ExposedDropdownMenu(
							modifier = Modifier.background(BrandColor.GRAY_50),
							expanded = expanded,
							onDismissRequest = { expanded = false }
						) {
							holdingAreas.forEach {
								DropdownMenuItem(
									text = { Text(it) },
									onClick = {
										onSelectedOptionTextChanged(it)
										expanded = false
									}
								)
							}
						}
					}
				}

				PrimaryButton(
					modifier = Modifier
						.fillMaxWidth(0.7f)
						.padding(top = 18.dp),
					text = stringResource(id = R.string.done).uppercase(),
					enabled = selectedHoldingArea.isNotEmpty(),
					onButtonClick = {
						onSubmitHoldingArea()
					}
				)

				if (isReprintActive) {
					TertiaryButton(
						modifier = Modifier
							.padding(start = 24.dp, top = 24.dp)
							.align(Alignment.Start),
						text = stringResource(id = R.string.reprint_hold_slip).uppercase(),
						onButtonClick = onReprintHoldSlip
					)
				}
			}
		}
	)
}

@Preview
@Composable
private fun SelectHoldingAreaCardPreview() {
	SelectHoldingAreaCard(
		stepNumber = "2",
		isActive = true,
		isComplete = false,
		holdingAreas = listOf(),
		selectedHoldingArea = "",
		isReprintActive = false
	)
}

@Preview
@Composable
private fun SelectHoldingAreaCardReprintPreview() {
	SelectHoldingAreaCard(
		stepNumber = "2",
		isActive = true,
		isComplete = false,
		holdingAreas = listOf(),
		selectedHoldingArea = "",
		isReprintActive = true
	)
}
