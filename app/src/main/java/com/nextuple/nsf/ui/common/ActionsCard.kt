package com.nextuple.nsf.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily

data class ActionsCardAction(
	val label: String,
	val isEnabled: Boolean,
	val onClick: () -> Unit
)

@Composable
fun ActionsCard(
	modifier: Modifier = Modifier,
	title: String,
	primaryAction: ActionsCardAction?,
	secondaryActions: List<ActionsCardAction>
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = BrandColor.GRAY_50)
	) {
		Column(
			modifier = Modifier
				.padding(12.dp)
				.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				modifier = Modifier.align(Alignment.Start),
				text = title,
				fontFamily = FontFamily.ARCHIVO,
				fontSize = 18.sp,
				fontWeight = FontWeight(700),
				letterSpacing = 0.5.sp
			)
			if (primaryAction != null) {
				PrimaryButton(
					modifier = Modifier
						.align(Alignment.CenterHorizontally)
						.fillMaxWidth(0.8f),
					text = primaryAction.label.uppercase(),
					enabled = primaryAction.isEnabled,
					onButtonClick = primaryAction.onClick
				)
			}
			secondaryActions.forEach {
				SecondaryButton(
					modifier = Modifier
						.align(Alignment.CenterHorizontally)
						.fillMaxWidth(0.8f),
					text = it.label.uppercase(),
					enabled = it.isEnabled,
					onButtonClick = it.onClick
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewActionsCard() {
	ActionsCard(
		title = "Pickup Actions",
		primaryAction = ActionsCardAction(label = "Start Pickup", isEnabled = true) {},
		secondaryActions = emptyList()
	)
}
