package com.nextuple.nsf.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

/**
 * TODO: Make this a bit more generic, customizable, and add tests.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopLabeledTextField(
	modifier: Modifier = Modifier,
	labelText: String,
	fieldValue: String,
	isPrefixEnabled: Boolean = false,
	prefixText: String = "",
	isInvalid: Boolean = false,
	onValueChange: (newValue: String) -> Unit,
	errorMessage: String = "",
	textFieldShape: Shape = RoundedCornerShape(4.dp),
	keyboardActions: KeyboardActions = KeyboardActions { }
) {
	val interactionSource = remember { MutableInteractionSource() }

	Column(modifier = modifier) {
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.testTag("TopLabeledTextFieldLabelText"),
			text = labelText,
			textAlign = TextAlign.Start,
			color = if (isInvalid) BrandColor.RED_600 else BrandColor.GRAY_900,
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Normal,
			fontStyle = FontStyle.Normal,
			fontSize = 12.sp
		)

		if (isPrefixEnabled) {
			Row(
				modifier = Modifier
					.testTag("TopLabeledTextFieldFieldValue")
					.fillMaxWidth()
					.height(44.dp)
					.padding(top = 2.dp, bottom = 2.dp)
					.border(
						BorderStroke(
							width = 0.5.dp,
							color = if (isInvalid) BrandColor.RED_600 else BrandColor.GRAY
						),
						shape = textFieldShape
					),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					modifier = Modifier.padding(start = 8.dp),
					text = prefixText,
					fontSize = 14.sp,
					color = BrandColor.GRAY_600
				)
				BasicTextField(
					value = fieldValue,
					onValueChange = onValueChange,
					modifier = Modifier
						.testTag("TopLabeledTextFieldFieldValue"),
					singleLine = true,
					textStyle = TextStyle.Default.copy(
						fontWeight = FontWeight.Bold,
						fontSize = 14.sp
					),
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
					keyboardActions = keyboardActions,
					interactionSource = interactionSource
				) { innerTextField ->
					TextFieldDefaults.OutlinedTextFieldDecorationBox(
						value = fieldValue,
						innerTextField = innerTextField,
						singleLine = true,
						enabled = true,
						visualTransformation = VisualTransformation.None,
						interactionSource = interactionSource,
						contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
							top = 0.dp,
							bottom = 0.dp,
							start = 0.dp,
							end = 8.dp
						),
						colors = TextFieldDefaults.textFieldColors(
							containerColor = Color.White,
							cursorColor = BrandColor.GRAY_900,
							focusedIndicatorColor = Color.Transparent,
							unfocusedIndicatorColor = Color.Transparent
						)
					)
				}
			}
		} else {
			BasicTextField(
				value = fieldValue,
				onValueChange = onValueChange,
				modifier = Modifier
					.testTag("TopLabeledTextFieldFieldValue")
					.fillMaxWidth()
					.height(44.dp)
					.padding(top = 2.dp, bottom = 2.dp)
					.border(
						BorderStroke(
							width = 0.5.dp,
							color = if (isInvalid) BrandColor.RED_600 else BrandColor.GRAY
						),
						shape = textFieldShape
					),
				singleLine = true,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = keyboardActions,
				interactionSource = interactionSource
			) { innerTextField ->
				TextFieldDefaults.OutlinedTextFieldDecorationBox(
					value = fieldValue,
					innerTextField = innerTextField,
					singleLine = true,
					enabled = true,
					visualTransformation = VisualTransformation.None,
					interactionSource = interactionSource,
					contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
						top = 0.dp,
						bottom = 0.dp,
						start = 8.dp,
						end = 8.dp
					),
					colors = TextFieldDefaults.textFieldColors(
						containerColor = Color.White,
						cursorColor = BrandColor.GRAY_900,
						focusedIndicatorColor = Color.Transparent,
						unfocusedIndicatorColor = Color.Transparent
					)
				)
			}
		}
		Text(
			text = if (isInvalid) errorMessage else "",
			color = BrandColor.RED_600,
			modifier = Modifier
				.align(Alignment.End),
			fontFamily = FontFamily.ARCHIVO,
			fontWeight = FontWeight.Normal,
			fontStyle = FontStyle.Normal,
			fontSize = 11.sp
		)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewTopLabeledTextField() {
	TopLabeledTextField(
		labelText = "DKS Number",
		fieldValue = "dks123456",
		onValueChange = {}

	)
}
