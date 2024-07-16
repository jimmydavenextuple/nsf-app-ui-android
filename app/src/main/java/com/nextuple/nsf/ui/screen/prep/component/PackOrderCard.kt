package com.nextuple.nsf.ui.screen.prep.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.ButtonState
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.component.ExpandableStepCard
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun PackOrderCard(
	isActive: Boolean,
	isComplete: Boolean,
	onPackOrder: () -> Unit = {}
) {
	ExpandableStepCard(
		stepNumber = "1",
		title = stringResource(id = R.string.pack_order),
		isActive = isActive,
		isComplete = isComplete,
		extraContent = {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Image(
					modifier = Modifier.padding(vertical = 10.dp),
					imageVector = ImageVector.vectorResource(R.drawable.pack_and_hold),
					contentDescription = stringResource(id = R.string.pack_order)
				)
				Text(
					modifier = Modifier.padding(horizontal = 16.dp),
					text = stringResource(id = R.string.pack_info),
					style = TextStyle(
						fontSize = 12.sp,
						fontWeight = FontWeight(400),
						color = BrandColor.BLACK,
						letterSpacing = 0.5.sp
					)
				)
				PrimaryButton(
					modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
					text = stringResource(id = R.string.print_hold_slip),
					buttonState = ButtonState.DEFAULT,
					onButtonClick = onPackOrder
				)
			}
		}
	)
}

@Preview
@Composable
private fun PackOrderCardPreview() {
	PackOrderCard(
		isActive = true,
		isComplete = false,
		onPackOrder = {}
	)
}
