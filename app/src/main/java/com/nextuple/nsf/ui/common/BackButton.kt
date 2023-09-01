package com.nextuple.nsf.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.R

@Composable
fun BackButton(
	modifier: Modifier = Modifier,
	onBackButtonClick: () -> Unit
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			modifier = Modifier
				.padding(start = 24.dp, end = 8.dp)
				.clickable { onBackButtonClick() },
			painter = painterResource(id = R.drawable.ic_back_arrow),
			contentDescription = "back button"
		)

		TertiaryButton(text = stringResource(id = R.string.back)) {
			onBackButtonClick()
		}
	}
}
