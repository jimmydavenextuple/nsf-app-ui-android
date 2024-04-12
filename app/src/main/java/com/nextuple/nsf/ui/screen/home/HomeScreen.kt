package com.nextuple.nsf.ui.screen.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.component.EmptyStateScreen
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.util.SubFulfillmentType
import com.nextuple.nsf.util.TimeUtils
import java.time.Instant

@Composable
fun HomeScreen() {
	val context = LocalContext.current

	BackHandler {
		val activity = context as? Activity
		activity?.finish()
	}

	if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
		Column {
			Column(Modifier.padding(start = 16.dp, top = 8.dp)) {
				Text(
					text = "Performance Metrics",
					style = TextStyle(
						fontFamily = FontFamily.ARCHIVO,
						lineHeight = 26.sp,
						fontSize = 20.sp,
						fontWeight = FontWeight(700)
					)
				)
				Text(
					text = TimeUtils.formatTimeStamp(Instant.now()),
					style = TextStyle(
						fontFamily = FontFamily.ARCHIVO,
						lineHeight = 12.sp,
						fontSize = 12.sp,
						fontWeight = FontWeight(500)
					)
				)
			}
			MetricsScreen(fulfillmentType = SubFulfillmentType.BOPIS)
		}
	} else {
	EmptyStateScreen(
		title = stringResource(id = R.string.under_construction_title),
		body = stringResource(id = R.string.under_construction_info),
		imageVector = ImageVector.vectorResource(id = R.drawable.under_construction)
	)
	}
}

@Composable
@PreviewPdt
fun PreviewHomeScreen() {
	HomeScreen()
}
