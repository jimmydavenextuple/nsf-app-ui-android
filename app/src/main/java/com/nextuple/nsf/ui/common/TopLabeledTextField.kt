package com.nextuple.nsf.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

/**
 * TODO: Make this a bit more generic, customizable, and add tests.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopLabeledTextField(
	modifier: Modifier = Modifier,
	labelColor: Color = BrandColor.GRAY_900,
	labelText: String,
	hintText: String,
	fieldValue: String,
	isPrefixEnabled: Boolean = false,
	prefixText: String = "",
	isInvalid: Boolean = false,
	onValueChange: (newValue: String) -> Unit,
	errorMessage: String = "",
	textFieldShape: Shape = RoundedCornerShape(8.dp),
	keyboardActions: KeyboardActions = KeyboardActions { }
) {
	val interactionSource = remember { MutableInteractionSource() }
	val errorColor = BrandColor.RED_600
	val errorTextColor = BrandColor.RED_500
	val borderStroke = BorderStroke(
		width = 0.5.dp,
		color = if (isInvalid) errorColor else BrandColor.GRAY_700
	)

	Column(modifier = modifier.background(color = Color.Transparent)) {
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.testTag("TopLabeledTextFieldLabelText"),
			text = labelText,
			textAlign = TextAlign.Start,
			color = if (isInvalid) errorColor else labelColor,
			fontWeight = FontWeight.Bold,
			fontStyle = FontStyle.Normal,
			fontSize = 12.sp
		)

		Row(
			modifier = Modifier
				.testTag("TopLabeledTextFieldFieldValue")
				.fillMaxWidth()
				.height(44.dp)
				.padding(top = 2.dp, bottom = 2.dp)
				.border(
					border = borderStroke,
					shape = textFieldShape
				)
				.background(
					color = if (isInvalid) BrandColor.RED_100 else Color.White,
					// rounded corner to match with the OutlinedTextField
					shape = textFieldShape
				),
			verticalAlignment = Alignment.CenterVertically
		) {
			Spacer(modifier = Modifier.width(8.dp))
			if (isPrefixEnabled) {
				Text(
					text = prefixText,
					fontSize = 14.sp,
					color = if (isInvalid) errorTextColor else BrandColor.GRAY_600
				)
			}

			var textStyle = if (isPrefixEnabled) {
				TextStyle.Default.copy(
					fontWeight = FontWeight.Bold,
					fontSize = 14.sp
				)
			} else {
				TextStyle.Default
			}

			textStyle = if (isInvalid) {
				textStyle.copy(color = errorTextColor)
			} else {
				textStyle
			}
			BasicTextField(
				modifier = Modifier
					.weight(1f)
					.testTag("TopLabeledTextFieldFieldValue"),
				textStyle = textStyle,
				value = fieldValue,
				onValueChange = onValueChange,
				singleLine = true,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = keyboardActions,
				interactionSource = interactionSource
			) { innerTextField ->
				val containerColor = Color.Transparent
				OutlinedTextFieldDefaults.DecorationBox(
					value = fieldValue,
					innerTextField = innerTextField,
					enabled = true,
					singleLine = true,
					visualTransformation = VisualTransformation.None,
					interactionSource = interactionSource,
					placeholder = {
						Text(
							text = hintText,
							fontSize = 12.sp,
							color = BrandColor.VERY_DARY_GREY,
							letterSpacing = 1.sp
						)
					},
					colors = TextFieldDefaults.colors(
						focusedContainerColor = containerColor,
						unfocusedContainerColor = containerColor,
						disabledContainerColor = containerColor,
						cursorColor = BrandColor.GRAY_900,
						focusedIndicatorColor = Color.Transparent,
						unfocusedIndicatorColor = Color.Transparent
					),
					contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
						start = 0.dp,
						top = 0.dp,
						end = 8.dp,
						bottom = 0.dp
					)
				)
			}
			if (isInvalid) {
				Icon(
					modifier = Modifier
						.padding(8.dp),
					imageVector = ImageVector.vectorResource(R.drawable.ic_alert_error_filled),
					tint = errorColor,
					contentDescription = "Error Icon"
				)
			}
		}
		Text(
			modifier = Modifier.padding(horizontal = 8.dp),
			text = if (isInvalid) errorMessage else "",
			color = errorColor,
			fontWeight = FontWeight.Bold,
			fontStyle = FontStyle.Italic,
			fontSize = 11.sp
		)
	}
}

@Composable
@Preview(showBackground = true)
private fun PreviewTopLabeledTextField() {
	TopLabeledTextField(
		hintText = "User Id",
		labelText = "User Id",
		fieldValue = "userId123456",
		onValueChange = {}
	)
}

@Composable
@Preview(showBackground = true)
private fun PreviewTopLabeledTextFieldError() {
	TopLabeledTextField(
		hintText = "User Id",
		labelText  = "User Id",
		fieldValue = "userId123456",
		isInvalid = true,
		errorMessage = "Invalid Userd Id",
		onValueChange = {}
	)
}

@Composable
@Preview(showBackground = true)
private fun PreviewTopLabeledTextFieldPrefix() {
	TopLabeledTextField(
		hintText = "Printer IP",
		labelText = "Printer IP",
		fieldValue = "101",
		isPrefixEnabled = true,
		prefixText = "192.0.1.",
		onValueChange = {}
	)
}

@Composable
@Preview(showBackground = true)
private fun PreviewTopLabeledTextFieldPrefixError() {
	TopLabeledTextField(
		hintText = "Printer IP",
		labelText = "Printer IP",
		fieldValue = "101",
		isPrefixEnabled = true,
		prefixText = "192.0.1.",
		isInvalid = true,
		errorMessage = "Lorem ipsum dolor sit amet consectetur adipisicing elit. Maxime mollitia,\n" +
			"optio, eaque rerum! Provident similique accusantium nemo autem. Veritatis",
		onValueChange = {}
	)
}
