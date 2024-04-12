package com.nextuple.nsf.ui.screen.prep.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun PrepDetailScaffold(
	athleteName: String,
	orderNumber: String,
	content: @Composable ColumnScope.() -> Unit
) {
	Column(
		modifier = Modifier
			.background(BrandColor.GRAY_100)
	) {
		PrepHeader(
			modifier = Modifier
				.fillMaxWidth()
				.background(BrandColor.GRAY_50),
			athlete = athleteName,
			orderNum = orderNumber
		)
		Column(
			modifier = Modifier
				.padding(horizontal = 16.dp, vertical = 8.dp)
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
		) {
			content()
		}
	}
}
