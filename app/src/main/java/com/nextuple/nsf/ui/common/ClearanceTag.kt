package com.nextuple.nsf.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun ClearanceTag(modifier: Modifier = Modifier, clearanceColor: String, colorDesc: String) {
	val rgbList = clearanceColor.split(",")
	val rgbNums = rgbList.map { it.toInt() }

	val color = Color(rgbNums[0], rgbNums[1], rgbNums[2])

	if (colorDesc.contains("purple", true) || color == Color(112, 48, 160)) {
		Image(painter = painterResource(id = R.drawable.ic_clearance_purple), contentDescription = "Purple Clearance Tag")
	} else if (colorDesc.contains("yellow", true) || color == Color(255, 165, 10)) {
		Image(painter = painterResource(id = R.drawable.ic_clearance_yellow), contentDescription = "Yellow Clearance Tag")
	} else if (colorDesc.contains("green", true) || color == Color(0, 175, 65)) {
		Image(painter = painterResource(id = R.drawable.ic_clearance_green), contentDescription = "Green Clearance Tag")
	} else if (colorDesc.contains("pink", true) || color == Color(244, 134, 200)) {
		Image(painter = painterResource(id = R.drawable.ic_clearance_pink), contentDescription = "Pink Clearance Tag")
	} else {
		Box(
			modifier = modifier
				.clip(CircleShape)
				.border(width = 1.dp, color = BrandColor.BLUE_300, shape = CircleShape)
				.background(color = BrandColor.GRAY_50)
		) {
			Row(
				modifier = Modifier
					.padding(horizontal = 8.dp, vertical = 4.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Center
			) {
				Text(
					modifier = Modifier
						.testTag("Clearance"),
					text = "CLEARANCE",
					fontSize = 10.sp,
					color = BrandColor.BLUE_600,
					fontFamily = FontFamily.ARCHIVO,
					fontWeight = FontWeight.Bold,
					letterSpacing = 0.5.sp
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewPurpleClearanceTag() {
	ClearanceTag(Modifier, "112,48,160", "PURPLE")
}

@Preview(showBackground = true)
@Composable
fun PreviewGreenClearanceTag() {
	ClearanceTag(Modifier, "0,175,65", "GREEN")
}

@Preview(showBackground = true)
@Composable
fun PreviewYellowClearanceTag() {
	ClearanceTag(Modifier, "255,165,10", "YELLOW")
}

@Preview(showBackground = true)
@Composable
fun PreviewPinkClearanceTag() {
	ClearanceTag(Modifier, "244,134,200", "PINK")
}

@Preview(showBackground = true)
@Composable
fun PreviewOtherClearanceTag() {
	ClearanceTag(Modifier, "115,147,179", "BLUE")
}
