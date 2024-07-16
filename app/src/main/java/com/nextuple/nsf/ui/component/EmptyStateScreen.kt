package com.nextuple.nsf.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.PreviewPdt

@Composable
fun EmptyStateScreen(title: String, body: String, imageVector: ImageVector) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(BrandColor.WHITE),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Image(
			imageVector = imageVector,
			contentDescription = "No Items on this screen"
		)

		Text(
			modifier = Modifier
				.width(215.dp)
				.padding(top = 10.dp),
			text = title,
			fontSize = 20.sp,
			fontWeight = FontWeight.Bold,
			color = BrandColor.GRAY_900,
			letterSpacing = 0.5.sp
		)
		Text(
			modifier = Modifier
				.width(300.dp)
				.padding(top = 10.dp),
			text = body,
			fontSize = 14.sp,
			lineHeight = 16.sp,
			fontWeight = FontWeight.Normal,
			color = BrandColor.GRAY_900,
			letterSpacing = 0.5.sp
		)
	}
}

@Composable
@PreviewPdt
fun PreviewEmptyStateScreen() {
	EmptyStateScreen(
		title = stringResource(id = R.string.under_construction_title),
		body = stringResource(id = R.string.under_construction_info),
		imageVector = ImageVector.vectorResource(id = R.drawable.under_construction)
	)
}
