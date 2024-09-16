package com.nextuple.nsf.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniTextField(
	modifier: Modifier = Modifier,
	icon: ImageVector? = null,
	iconTint: Color = BrandColor.DARK_BLUE,
	fieldValue: String,
	hintText: String = "",
	onValueChange: (newValue: String) -> Unit,
	textFieldShape: Shape = RoundedCornerShape(4.dp),
	keyboardActions: KeyboardActions = KeyboardActions { },
	borderStroke: BorderStroke = BorderStroke(
		width = 0.5.dp,
		color = BrandColor.WHITE
	),
	onClear: () -> Unit = {}
) {
	val interactionSource = remember { MutableInteractionSource() }

	Column(modifier = modifier) {
		Row(
			modifier = Modifier
				.testTag("OmniTextFieldFieldValue")
				.fillMaxWidth()
				.height(36.dp)
				.border(border = borderStroke, shape = textFieldShape)
				.background(color = BrandColor.WHITE, shape = textFieldShape),
			verticalAlignment = Alignment.CenterVertically
		) {
			icon?.let {
				Icon(
					modifier = Modifier
						.testTag("OmniTextFieldIcon")
						.padding(start = 12.dp)
						.size(20.dp),
					imageVector = icon,
					tint = iconTint,
					contentDescription = "Leading icon"
				)
			}

			BasicTextField(
				value = fieldValue,
				onValueChange = onValueChange,
				modifier = Modifier
					.testTag("OmniTextFieldFieldValue")
					.weight(1.0f),
				singleLine = true,
				textStyle = TextStyle.Default.copy(
					fontWeight = FontWeight.Normal,
					color = BrandColor.GRAY_900,
					letterSpacing = 0.5.sp,
					fontSize = 14.sp
				),
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = keyboardActions,
				interactionSource = interactionSource
			) { innerTextField ->
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
							fontWeight = FontWeight.Bold,
							color = BrandColor.GRAY_550,
							letterSpacing = 1.sp
						)
					},
					colors = TextFieldDefaults.colors(
						focusedContainerColor = Color.Transparent,
						unfocusedContainerColor = Color.Transparent,
						disabledContainerColor = Color.Transparent,
						cursorColor = BrandColor.GRAY_900,
						focusedIndicatorColor = Color.Transparent,
						unfocusedIndicatorColor = Color.Transparent
					),
					contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
						start = 16.dp,
						top = 0.dp,
						end = 8.dp,
						bottom = 0.dp
					)
				)
			}

			// Clear Button
			if (fieldValue.isNotEmpty()) {
				IconButton(
					modifier = Modifier
						.testTag("OmniTextFieldClear")
						.padding(end = 12.dp)
						.size(16.dp),
					onClick = {
						onClear.invoke()
						onValueChange("")
					}
				) {
					Icon(
						imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
						tint = BrandColor.GRAY_900,
						contentDescription = "Clear icon"
					)
				}
			}
		}
	}
}

@Composable
@Preview(showBackground = false)
fun PreviewOmniTextField() {
	OmniTextField(
		icon = ImageVector.vectorResource(id = R.drawable.ic_search),
		iconTint = BrandColor.DARK_BLUE,
		fieldValue = "Roosevelt",
		onValueChange = {},
		onClear = {}
	)
}

@Composable
@Preview(showBackground = false)
fun PreviewOmniTextFieldHint() {
	OmniTextField(
		icon = ImageVector.vectorResource(id = R.drawable.ic_search),
		iconTint = BrandColor.DARK_BLUE,
		fieldValue = "",
		hintText = stringResource(id = R.string.search_hint).uppercase(),
		onValueChange = {},
		onClear = {}
	)
}

@Composable
@Preview(showBackground = false)
fun PreviewOmniTextFieldSimple() {
	OmniTextField(
		fieldValue = "Roosevelt",
		hintText = stringResource(id = R.string.search_hint).uppercase(),
		onValueChange = {},
		onClear = {}
	)
}
