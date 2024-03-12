package com.nextuple.nsf.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.common.chip.ElapsedTimeChip
import com.nextuple.nsf.ui.common.chip.OrderStatusChip
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

@Composable
fun InfoCard(
	modifier: Modifier = Modifier,
	title: String = "",
	shape: Shape = RoundedCornerShape(12.dp),
	internalPadding: Dp = 12.dp,
	internalSpacedBy: Dp = 8.dp,
	labelsToValues: LinkedHashMap<String, String>,
	status: String? = null,
	athleteCheckInTime: String? = null,
	content: @Composable () -> Unit = {}
) {
	val rows by remember {
		mutableStateOf(labelsToValues.filter { it.value.isNotEmpty() }.toList().chunked(2))
	}

	Card(
		modifier = modifier,
		shape = shape,
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(
			modifier = Modifier.padding(internalPadding)
		) {
			if (title.isNotEmpty()) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						modifier = Modifier.padding(bottom = 8.dp),
						text = title,
						fontFamily = FontFamily.ARCHIVO,
						fontSize = 18.sp,
						fontWeight = FontWeight(700),
						letterSpacing = 0.5.sp
					)

					Row(
						horizontalArrangement = Arrangement.spacedBy(4.dp)
					) {
						status?.let {
							OrderStatusChip(statusText = status)
						}

						athleteCheckInTime?.let {
							ElapsedTimeChip(athleteCheckInTime = athleteCheckInTime)
						}
					}
				}
			}

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 16.dp),
				verticalArrangement = Arrangement.spacedBy(internalSpacedBy)
			) {
				rows.forEach { row ->
					Row(
						modifier = Modifier.fillMaxWidth()
					) {
						row.forEach { (label, value) ->
							TextInfo(modifier = Modifier.weight(1f), label = label, value = value)
						}
					}
				}
			}

			content()
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewInfoCard() {
	InfoCard(
		title = "Customer Info",
		status = "Ready",
		labelsToValues = linkedMapOf(
			"Name" to "Erica Franco",
			"Proxy Name" to "Moyses Franco",
			"Phone Number" to "(508) 769 - 5491"
		)
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewInfoCardCheckIn() {
	InfoCard(
		title = "Customer Info",
		status = "Checked In",
		athleteCheckInTime = "3:06",
		labelsToValues = linkedMapOf(
			"Name" to "Erica Franco",
			"Proxy Name" to "Moyses Franco",
			"Phone Number" to "(508) 769 - 5491"
		)
	)
}
