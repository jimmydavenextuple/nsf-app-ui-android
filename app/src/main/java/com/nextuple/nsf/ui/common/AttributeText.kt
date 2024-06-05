package com.nextuple.nsf.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun AttributeText(
	modifier: Modifier = Modifier,
	label: String,
	value: String?,
	fontSize: TextUnit = 14.sp,
	useEmptyImage: Boolean = false,
	iconImageVector: ImageVector? = null,
	valueMaxLines: Int = Int.MAX_VALUE
) {
	Row(
		modifier = modifier.height(18.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = label.uppercase(),
			fontFamily = FontFamily.ARCHIVO,
			fontSize = fontSize,
			fontWeight = FontWeight(700),
			letterSpacing = 1.5.sp
		)
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = value.orEmpty(),
				fontFamily = FontFamily.ARCHIVO,
				fontSize = fontSize,
				fontWeight = FontWeight(400),
				letterSpacing = 0.5.sp,
				maxLines = valueMaxLines
			)

			val imageModifier = Modifier
				.size(18.dp)
				.padding(start = 4.dp)

			if (useEmptyImage) {
				Box(modifier = imageModifier)
			}

			iconImageVector?.let {
				Image(
					modifier = imageModifier.align(Alignment.CenterVertically),
					imageVector = iconImageVector,
					contentDescription = "Text Info Icon"
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewAttributeText() {
	AttributeText(label = "label", value = "value value value", fontSize = 14.sp)
}

@Preview(showBackground = true)
@Composable
fun PreviewAttributeTextWithIcon() {
	AttributeText(
		label = "label",
		value = "value value value",
		iconImageVector = ImageVector.vectorResource(R.drawable.ic_location),
		fontSize = 14.sp
	)
}
