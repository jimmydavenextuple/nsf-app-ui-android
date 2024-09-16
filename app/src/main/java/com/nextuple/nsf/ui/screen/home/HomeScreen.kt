package com.nextuple.nsf.ui.screen.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextuple.nsf.retrofit.dto.response.MetricsSummaryResponse
import com.nextuple.nsf.ui.state.HomeViewModel
import com.nextuple.nsf.ui.state.MetricsSummaryData
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.util.SubFulfillmentType
import com.nextuple.nsf.util.TimeUtils
import java.time.Instant

@Composable
fun HomeScreen(
	homeViewModel: HomeViewModel = hiltViewModel()
) {
	val context = LocalContext.current

	BackHandler {
		val activity = context as? Activity
		activity?.finish()
	}

	HomeScreenComponent(
		metricsSummaryData = homeViewModel.metricsSummaryData,
		setMetricSummary = homeViewModel::setMetricsSummary
	)
}

@Composable
private fun HomeScreenComponent(
	metricsSummaryData: MetricsSummaryData,
	setMetricSummary: () -> Unit = {}
) {
	val bopisMetrics = metricsSummaryData.data?.bopis
	LaunchedEffect(Unit) {
		setMetricSummary()
	}
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = BrandColor.GRAY_100)
	) {
		Column(
			modifier = Modifier
				.background(color = BrandColor.GRAY_50)
		) {
			MetricsHeader()
			MetricsScreen(
				fulfillmentType = SubFulfillmentType.BOPIS,
				fillRatePercentage = bopisMetrics?.fillRatePercentage,
				productivityPercentage = bopisMetrics?.productivityPercentage,
				unitsWorked = bopisMetrics?.unitsWorkedInTime,
				totalUnits = bopisMetrics?.totalUnitsWorked,
				uph = bopisMetrics?.pickUph,
				curbsideDeliverySpeed = bopisMetrics?.curbsideDeliverySpeed,
				inStoreDeliverySpeed = bopisMetrics?.inStoreDeliverySpeed
			)
		}
		if (metricsSummaryData.state == GenericViewState.Loading) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center),
				color = BrandColor.GRAY_900
			)
		}
	}
}

@Composable
private fun MetricsHeader() {
	Column(Modifier.padding(start = 16.dp, top = 8.dp)) {
		Text(
			text = "Performance Metrics",
			style = TextStyle(
				lineHeight = 26.sp,
				fontSize = 20.sp,
				fontWeight = FontWeight(700)
			)
		)
		Text(
			text = TimeUtils.formatTimeStamp(Instant.now()),
			style = TextStyle(
				lineHeight = 12.sp,
				fontSize = 12.sp,
				fontWeight = FontWeight(500)
			)
		)
	}
}

@Composable
@PreviewPdt
fun PreviewHomeScreen() {
	HomeScreenComponent(
		metricsSummaryData = MetricsSummaryData(
			data = MetricsSummaryResponse(
				bopis = MetricsSummaryResponse.OrderMetrics(
					fillRatePercentage = 97.3,
					productivityPercentage = 89.1,
					unitsWorkedInTime = 109,
					totalUnitsWorked = 122,
					pickUph = 8.1,
					curbsideDeliverySpeed = "00:02:21.012",
					inStoreDeliverySpeed = "00:03:37.012"
				),
				bopl = MetricsSummaryResponse.OrderMetrics()
			)
		)
	)
}
