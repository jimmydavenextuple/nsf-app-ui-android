package com.nextuple.nsf.ui.screen.pickup.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.HeaderText
import com.nextuple.nsf.ui.common.chip.OrderStatusChip
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.PreviewPdtWidth

@Composable
fun PickupHeader(
	modifier: Modifier = Modifier,
	driver: String,
	checkedInTime: String
) {
	Column(
		modifier = modifier
	) {
		Row(
			modifier = Modifier
				.padding(horizontal = 24.dp, vertical = 8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				modifier = Modifier
					.padding(top = 2.dp, end = 8.dp)
					.align(Alignment.Top),
				painter = painterResource(id = R.drawable.ic_doordash_logo),
				contentDescription = "Doordash logo"
			)
			HeaderText(
				title = stringResource(id = R.string.driver).uppercase(),
				value = driver
			)
			Spacer(modifier = Modifier.weight(1f))
			OrderStatusChip(statusText = checkedInTime)
			Spacer(modifier = Modifier.width(4.dp))
			OrderStatusChip(statusText = "Checked In")
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
private fun PreviewPickupHeader() {
	PickupHeader(driver = "Andrew D.", checkedInTime = "2:06")
}
