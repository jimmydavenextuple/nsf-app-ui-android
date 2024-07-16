package com.nextuple.nsf.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun SecondaryButton(
	modifier: Modifier = Modifier,
	text: String,
	buttonColor: Color = Color.Transparent,
	textColor: Color = BrandColor.GRAY_900,
	textSize: TextUnit = 16.sp,
	buttonShape: Shape = RoundedCornerShape(4.dp),
	enabled: Boolean = true,
	buttonState: ButtonState = ButtonState.DEFAULT,
	onButtonClick: () -> Unit
) {
	Button(
		modifier = modifier
			.height(IntrinsicSize.Min)
			.testTag("SecondaryButton"),
		onClick = onButtonClick,
		border = BorderStroke(2.dp, if (enabled) textColor else BrandColor.GRAY_500),
		shape = buttonShape,
		colors = ButtonDefaults.buttonColors(
			containerColor = buttonColor,
			disabledContainerColor = BrandColor.GRAY_100,
			contentColor = textColor,
			disabledContentColor = BrandColor.GRAY_600
		),
		enabled = enabled
	) {
		when (buttonState) {
			ButtonState.LOADING -> {
				CircularProgressIndicator(
					modifier = Modifier.size(30.dp),
					color = textColor
				)
			}

			else -> {
				Text(
					text = text,
					textAlign = TextAlign.Center,
					fontSize = textSize,
					letterSpacing = 1.5.sp,
					fontWeight = FontWeight.Bold
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun SecondaryButtonPreview() {
	SecondaryButton(text = "SECONDARY", enabled = true) { }
}
