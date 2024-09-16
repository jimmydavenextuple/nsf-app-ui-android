package com.nextuple.nsf.ui.common.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R.drawable
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun RemovableChip(
	modifier: Modifier = Modifier,
	text: String,
	onRemove: () -> Unit
) {
	Row(
		modifier = modifier
			.background(color = BrandColor.DARK_BLUE, shape = RoundedCornerShape(4.dp))
			.padding(8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = text,
			color = BrandColor.GRAY_50,
			fontWeight = FontWeight(700),
			fontSize = 10.sp,
			letterSpacing = 1.sp
		)

		Icon(
			modifier = Modifier
				.size(14.dp)
				.clickable { onRemove() },
			imageVector = ImageVector.vectorResource(id = drawable.ic_close),
			tint = BrandColor.GRAY_50,
			contentDescription = "Remove Icon"
		)
	}
}

@Composable
@Preview
fun PreviewRemovableChip() {
	RemovableChip(text = "PACK") {}
}
