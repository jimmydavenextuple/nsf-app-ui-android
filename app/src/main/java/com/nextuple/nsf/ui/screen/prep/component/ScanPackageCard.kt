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
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToAction
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.component.ExpandableStepCard
import com.nextuple.nsf.ui.util.GenericViewState

@Composable
fun ScanPackageCard(
	isStepActive: Boolean,
	isComplete: Boolean,
	holdSlipState: GenericViewState,
	onScanClick: () -> Unit
) {
	ExpandableStepCard(
		stepNumber = "2",
		title = stringResource(R.string.scan_package),
		isActive = isStepActive,
		isComplete = isComplete,
		extraContent = {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = stringResource(R.string.scan_place_hold),
					style = TextStyle(
						fontSize = 12.sp,
						fontWeight = FontWeight(400),
						lineHeight = 13.06.sp
					)
				)
					Image(
						modifier = Modifier.padding(top = 10.dp, start = 100.dp, bottom = 10.dp),
						imageVector = ImageVector.vectorResource(R.drawable.scan_and_hold),
						contentDescription = "Scanning Box"
					)
				DetailedCallToAction(
					modifier = Modifier
						.align(Alignment.CenterHorizontally),
					detailedCallToActionMode = when (holdSlipState) {
						is GenericViewState.Loading -> DetailedCallToActionMode.Loading()
						is GenericViewState.Success -> {
							DetailedCallToActionMode.Done()
						}

						else -> {
							DetailedCallToActionMode.Scan(stringResource(R.string.scan_package))
						}
					},
					onClick = onScanClick
				)
			}
		}
	)
}

@Preview
@Composable
private fun ScanPackageCardPreview() {
	ScanPackageCard(
		isStepActive = true,
		isComplete = false,
		holdSlipState = GenericViewState.Idle,
		onScanClick = { }
	)
}
