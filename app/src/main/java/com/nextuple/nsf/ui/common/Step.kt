package com.nextuple.nsf.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun Step(
	modifier: Modifier = Modifier,
	size: Dp = 28.dp,
	text: String?,
	isActive: Boolean,
	activeTextColor: Color = BrandColor.GRAY_50,
	inactiveTextColor: Color = BrandColor.GRAY_500,
	activeColor: Color = BrandColor.BLACK,
	inactiveColor: Color = BrandColor.GRAY_500,
	isOutlined: Boolean = false
) {
	Box(
		modifier = modifier
			.size(size)
			.clip(CircleShape)
			.background(Color.White)
			.let {
				if (isActive && !isOutlined) {
					it.background(color = activeColor)
				} else if (isActive) {
					it.border(width = 2.dp, color = activeColor, shape = CircleShape)
				} else if (isOutlined) {
					it.border(width = 2.dp, color = inactiveColor, shape = CircleShape)
				} else {
					it.background(color = inactiveColor)
				}
			},
		contentAlignment = Alignment.Center
	) {
		if (text == null) {
			Box(
				modifier = Modifier
					.padding(6.dp)
					.wrapContentSize()
			) {
				if (isActive && !isOutlined) {
					Icon(
						modifier = Modifier.size(40.dp),
						imageVector = ImageVector.vectorResource(R.drawable.ic_check_white),
						tint = BrandColor.WHITE,
						contentDescription = null
					)
				} else if (isActive) {
					Icon(
						modifier = Modifier.size(40.dp),
						imageVector = ImageVector.vectorResource(R.drawable.ic_check),
						tint = activeColor,
						contentDescription = null
					)
				} else if (isOutlined) {
					Icon(
						modifier = Modifier.size(40.dp),
						imageVector = ImageVector.vectorResource(R.drawable.ic_check),
						tint = inactiveColor,
						contentDescription = null
					)
				} else {
					Icon(
						modifier = Modifier.size(40.dp),
						imageVector = ImageVector.vectorResource(R.drawable.ic_check_white),
						tint = BrandColor.WHITE,
						contentDescription = null
					)
				}
			}
		} else {
			val color = if (isActive && !isOutlined) {
				activeTextColor
			} else if (isOutlined && isActive) {
				activeColor
			} else if (isOutlined) {
				inactiveTextColor
			} else {
				activeTextColor
			}

			Text(
				text = text,
				color = color,
				fontSize = 20.sp,
				fontWeight = FontWeight(700)
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewStepActiveCheck() {
	Step(
		text = null,
		isActive = true
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewOutlinedStepActiveCheck() {
	Step(
		text = null,
		isActive = true,
		isOutlined = true,
		activeColor = BrandColor.BLUE_300_NT
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewStepInactiveCheck() {
	Step(
		text = null,
		isActive = false
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewStepOutlinedInactiveCheck() {
	Step(
		text = null,
		isActive = false,
		isOutlined = true
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewStepActiveNumber() {
	Step(
		text = "12",
		isActive = true
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewOutlinedStepActiveNumber() {
	Step(
		text = "12",
		isActive = true,
		isOutlined = true
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewStepInactiveNumber() {
	Step(
		text = "12",
		isActive = false
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewStepOutlineInactiveNumber() {
	Step(
		text = "12",
		isActive = false,
		isOutlined = true
	)
}
