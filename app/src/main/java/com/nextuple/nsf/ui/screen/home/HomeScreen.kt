package com.nextuple.nsf.ui.screen.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.component.EmptyStateScreen
import com.nextuple.nsf.ui.util.PreviewPdt

@Composable
fun HomeScreen() {
	val context = LocalContext.current

	BackHandler {
		val activity = context as? Activity
		activity?.finish()
	}

	EmptyStateScreen(
		title = stringResource(id = R.string.under_construction_title),
		body = stringResource(id = R.string.under_construction_info),
		imageVector = ImageVector.vectorResource(id = R.drawable.under_construction)
	)
}

@Composable
@PreviewPdt
fun PreviewHomeScreen() {
	HomeScreen()
}
