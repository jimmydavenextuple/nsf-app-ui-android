package com.nextuple.nsf.ui.common

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun PrimaryButton(
	modifier: Modifier = Modifier,
	text: String,
	buttonColor: Color = BrandColor.DARK_BLUE,
	contentColor: Color = BrandColor.WHITE,
	buttonShape: Shape = RoundedCornerShape(0.dp),
	enabled: Boolean = true,
	textSize: TextUnit = 14.sp,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	buttonState: ButtonState = ButtonState.DEFAULT,
	onButtonClick: () -> Unit
) {
	Button(
		modifier = modifier
			.height(IntrinsicSize.Min)
			.alpha(if (enabled) 1f else .65f)
			.testTag("PrimaryButton"),
		onClick = onButtonClick,
		shape = buttonShape,
		colors = ButtonDefaults.buttonColors(
			containerColor = if (buttonState == ButtonState.DONE) {
				BrandColor.DARK_BLUE
			} else {
				buttonColor
			},
			disabledContainerColor = BrandColor.DARK_BLUE,
			contentColor = contentColor,
			disabledContentColor = BrandColor.WHITE
		),
		contentPadding = contentPadding,
		enabled = enabled
	) {
		when (buttonState) {
			ButtonState.DONE -> {
				Icon(
					painter = painterResource(id = R.drawable.ic_check_white),
					contentDescription = "done icon"
				)
			}

			ButtonState.LOADING -> {
				CircularProgressIndicator(
					modifier = Modifier.size(30.dp),
					color = contentColor
				)
			}

			else -> {
				Text(
					text = text,
					textAlign = TextAlign.Center,
					fontSize = textSize,
					letterSpacing = 1.5.sp,
					fontWeight = FontWeight(700)
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
	PrimaryButton(text = "PRIMARY", enabled = true) { }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonDisablePreview() {
	PrimaryButton(text = "PRIMARY", enabled = false) { }
}