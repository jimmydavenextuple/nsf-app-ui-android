package com.nextuple.nsf.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun MultiOptionModal(
	title: String?,
	subTitle: String?,
	buttons: List<String>,
	buttonClick: (String) -> Unit,
	crossIconClick: () -> Unit,
	backgroundColor: Color = Color.White,
	shape: Shape = RoundedCornerShape(4.dp),
	dismissOnBackPress: Boolean = true,
	dismissOnClickOutside: Boolean = true,
	onDismissRequest: () -> Unit
) {
	Dialog(
		onDismissRequest = {
			onDismissRequest()
		},
		properties = DialogProperties(
			dismissOnBackPress = dismissOnBackPress,
			dismissOnClickOutside = dismissOnClickOutside
		)
	) {
		Surface(
			modifier = Modifier.verticalScroll(rememberScrollState()),
			shape = shape,
			color = backgroundColor
		) {
			Column(
				modifier = Modifier
					.padding(20.dp)
					.wrapContentHeight(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					if (title != null) {
						Text(
							text = title,
							style = TextStyle(
								fontSize = 20.sp,
								fontWeight = FontWeight.Bold,
								letterSpacing = 0.5.sp
							)
						)
						Icon(
							modifier = Modifier
								.align(Alignment.CenterVertically)
								.clickable { crossIconClick.invoke() },
							imageVector = ImageVector.vectorResource(R.drawable.ic_close),
							tint = BrandColor.GRAY_900,
							contentDescription = "close"
						)
					}
				}
				if (title != null) {
					Spacer(modifier = Modifier.height(19.dp))
				}
				if (subTitle != null) {
					Text(
						text = subTitle,
						style = TextStyle(
							fontSize = 16.sp,
							letterSpacing = 0.5.sp,
							fontWeight = FontWeight.Normal
						)
					)

					Spacer(modifier = Modifier.height(24.dp))
				}

				if (buttons.isNotEmpty()) {
					PrimaryButton(
						modifier = Modifier.fillMaxWidth(),
						text = buttons[0],
						buttonColor = BrandColor.DARK_BLUE,
						onButtonClick = { buttonClick(buttons[0]) },
						contentColor = BrandColor.GRAY_100
					)

					Spacer(modifier = Modifier.height(16.dp))

					for (index in 1 until buttons.size) {
						SecondaryButton(
							modifier = Modifier.fillMaxWidth(),
							text = buttons[index],
							onButtonClick = { buttonClick(buttons[index]) }
						)

						Spacer(modifier = Modifier.height(16.dp))
					}
				}
			}
		}
	}
}

@Composable
@Preview
fun MultiOptionModalPreview() {
	MultiOptionModal(
		title = "Unit Locations",
		subTitle = "This product has multiple locations assigned to it. Where did you pick this unit it from?",
		buttons = listOf("F1.S1.04A", "F1.S1.04B", "OTHER"),
		buttonClick = { },
		crossIconClick = { },
		onDismissRequest = { }
	)
}
