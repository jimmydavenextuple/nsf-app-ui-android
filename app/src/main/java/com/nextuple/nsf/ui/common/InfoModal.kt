package com.nextuple.nsf.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun InfoModal(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String? = null,
    buttonText: String,
    buttonState: ButtonState = ButtonState.DEFAULT,
    isButtonEnabled: Boolean = true,
    buttonClick: (shouldShowAgain: Boolean) -> Unit,
    crossIconClick: (shouldShowAgain: Boolean) -> Unit,
    backgroundColor: Color = Color.White,
    showCheckBox: Boolean = false,
    checkBoxText: String? = null,
    shape: Shape = RoundedCornerShape(4.dp),
    visualContent: @Composable () -> Unit = {},
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    onDismissRequest: (shouldShowAgain: Boolean) -> Unit
) {
	var checkedState by remember { mutableStateOf(false) }

	val shouldShowAgain = !showCheckBox || !checkedState

	Dialog(
		onDismissRequest = {
			onDismissRequest(shouldShowAgain)
		},
		properties = DialogProperties(
			dismissOnBackPress = dismissOnBackPress,
			dismissOnClickOutside = dismissOnClickOutside
		)
	) {
		Surface(
			shape = shape,
			color = backgroundColor
		) {
			Column(
				modifier = modifier.padding(20.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = title,
						style = TextStyle(
							fontSize = 20.sp,
							fontFamily = FontFamily.ARCHIVO,
							fontWeight = FontWeight.SemiBold
						)
					)
					Icon(
						modifier = Modifier
							.align(Alignment.CenterVertically)
							.clickable { crossIconClick.invoke(shouldShowAgain) }
							.semantics { testTag = "InfoModalCloseButton" },
						imageVector = ImageVector.vectorResource(R.drawable.ic_close),
						tint = BrandColor.GRAY_900,
						contentDescription = "close"
					)
				}
				subTitle?.let {
					Spacer(modifier = Modifier.height(16.dp))
					Text(
						text = subTitle,
						style = TextStyle(
							fontSize = 16.sp,
							letterSpacing = 0.03.sp,
							fontFamily = FontFamily.ARCHIVO,
							fontWeight = FontWeight.Normal
						)
					)
				}
				if (showCheckBox && checkBoxText?.isNotBlank() == true) {
					CheckboxComponent(checkBoxText, checkedState) { checked ->
						checkedState = checked
					}
				}

				Spacer(modifier = Modifier.height(16.dp))
				visualContent.invoke()
				Spacer(modifier = Modifier.height(16.dp))

				PrimaryButton(
					modifier = Modifier.fillMaxWidth(0.85f)
						.semantics { testTag = "InfoModalButton" },
					text = buttonText,
					buttonColor = BrandColor.PINK_NT,
					onButtonClick = {
						buttonClick.invoke(shouldShowAgain)
					},
					buttonState = buttonState,
					enabled = isButtonEnabled,
					contentColor = BrandColor.GRAY_100
				)
			}
		}
	}
}

@Composable
fun CheckboxComponent(
	checkBoxText: String,
	checkedState: Boolean,
	onCheckedChange: ((Boolean) -> Unit)?
) {
	Spacer(modifier = Modifier.height(16.dp))

	Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
		Checkbox(
			checked = checkedState,
			onCheckedChange = onCheckedChange,
			colors = CheckboxDefaults.colors(
				checkedColor = BrandColor.PINK_NT,
				uncheckedColor = BrandColor.GRAY_600
			),
			modifier = Modifier.absoluteOffset(x = (-12).dp)
		)
		Text(
			text = checkBoxText,
			style = TextStyle(
				fontSize = 14.sp,
				letterSpacing = 0.03.sp,
				fontFamily = FontFamily.ARCHIVO,
				fontWeight = FontWeight.Normal
			),
			modifier = Modifier
				.align(Alignment.CenterVertically)
				.padding(start = 2.dp)
				.absoluteOffset(x = (-12).dp)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun InfoModalPreview() {
	InfoModal(
		title = "Title",
		subTitle = "This is where the body text of your modal goes. You can use any height you may need for options.",
		buttonText = "OK",
		buttonClick = { },
		crossIconClick = { },
		showCheckBox = true,
		checkBoxText = "Don't show me again this.",
		visualContent = { },
		onDismissRequest = { }
	)
}

enum class ButtonState {
	DEFAULT,
	LOADING,
	DONE
}
