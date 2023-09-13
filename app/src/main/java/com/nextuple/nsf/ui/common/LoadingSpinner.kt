package com.nextuple.nsf.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.ui.theme.BrandColor

/**
 * LoadingSpinner is a common component, will be be displayed during any operation eg: api calls
 */
@Composable
fun LoadingSpinner(
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier
			.wrapContentSize()
			.testTag("loadingSpinner")
			.clip(CircleShape)
			.background(color = BrandColor.PINK_NT)
	) {
		CircularProgressIndicator(
			modifier = Modifier
				.wrapContentSize()
				.padding(24.dp),
			color = BrandColor.PINK_100_NT,
			strokeWidth = 6.dp
		)
	}
}

@Composable
@Preview
fun PreviewLoadingSpinner() {
	LoadingSpinner(modifier = Modifier.size(76.dp))
}
