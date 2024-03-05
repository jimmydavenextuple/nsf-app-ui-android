package com.nextuple.nsf.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun BackButton(
	modifier: Modifier = Modifier,
	text: String = stringResource(id = R.string.back),
	contentDescription: String = "back button",
	onBackButtonClick: () -> Unit
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			modifier = Modifier.clickable { onBackButtonClick() },
			painter = painterResource(id = R.drawable.ic_back_arrow),
			contentDescription = contentDescription,
			tint = BrandColor.BLUE_300_NT
		)

		TertiaryButton(text = text) {
			onBackButtonClick()
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewBackButton() {
	BackButton {}
}
