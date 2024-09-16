package com.nextuple.nsf.ui.screen.prep.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.HeaderText
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.PreviewPdtWidth

@Composable
fun PrepHeader(
	modifier: Modifier = Modifier,
	athlete: String,
	orderNum: String
) {
	Column(modifier = modifier) {
		Row(
			modifier = Modifier
				.padding(horizontal = 32.dp, vertical = 8.dp)
		) {
			HeaderText(
				modifier = Modifier
					.fillMaxWidth(.5f),
				title = stringResource(id = R.string.athlete),
				value = athlete
			)
			Spacer(modifier = Modifier.weight(1f))
			HeaderText(
				title = stringResource(id = R.string.order_num),
				value = orderNum
			)
		}
		HorizontalDivider(
			modifier = Modifier,
			thickness = 1.dp,
			color = BrandColor.GRAY_350
		)
	}
}

@PreviewPdtWidth
@Composable
private fun PrepHeaderPreview() {
	PrepHeader(
		modifier = Modifier
			.fillMaxWidth()
			.background(BrandColor.GRAY_50),
		athlete = "Chester Arthur",
		orderNum = "XX345674902"
	)
}
