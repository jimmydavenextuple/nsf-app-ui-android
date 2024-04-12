package com.nextuple.nsf.ui.common.chip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.util.OrderStatus
import com.nextuple.nsf.util.TimeUtils
import com.nextuple.nsf.util.TimeUtils.formatTime
import kotlinx.coroutines.delay

@Composable
fun ElapsedTimeChip(
	modifier: Modifier = Modifier,
	athleteCheckInTime: String
) {
	var elapsedTime by remember {
		mutableLongStateOf(
			// take if used to only grab time if it is positive
			TimeUtils.calculateTimeDifferenceInSeconds(athleteCheckInTime)
				?.takeIf { it >= 0 } ?: 0
		)
	}

	LaunchedEffect(LocalLifecycleOwner.current) {
		while (true) {
			delay(1000)
			elapsedTime = elapsedTime.plus(1)
		}
	}

	OrderStatusChip(
		modifier = modifier,
		statusText = elapsedTime.formatTime(),
		backgroundColor = BrandColor.PINK_NT
	)
}

@Composable
@Preview(showBackground = true)
fun PreviewElapsedTimeChip() {
	ElapsedTimeChip(athleteCheckInTime = "3:06")
}
