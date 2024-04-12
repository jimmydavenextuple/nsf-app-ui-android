package com.nextuple.nsf.ui.screen.prep.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToAction
import com.nextuple.nsf.ui.common.callToAction.DetailedCallToActionMode
import com.nextuple.nsf.ui.component.ExpandableStepCard
import com.nextuple.nsf.ui.util.GenericViewState

@Composable
fun ScanLocationCard(
    isActive: Boolean,
    stepNumber: String,
    holdLocationState: GenericViewState,
    onScanClick: () -> Unit = {}
) {
	ExpandableStepCard(
		stepNumber = stepNumber,
		title = stringResource(id = R.string.scan_location),
		isActive = isActive,
		extraContent = {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Image(
					modifier = Modifier.padding(top = 8.dp),
					imageVector = ImageVector.vectorResource(R.drawable.scanning_bin),
					contentDescription = "Scanning Bin"
				)
				DetailedCallToAction(
					modifier = Modifier
						.align(Alignment.CenterHorizontally),
					detailedCallToActionMode = when (holdLocationState) {
						is GenericViewState.Loading -> DetailedCallToActionMode.Loading()
						is GenericViewState.Success -> {
							DetailedCallToActionMode.Done()
						}
						else -> {
							DetailedCallToActionMode.Scan(stringResource(id = R.string.scan_location))
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
private fun ScanLocationCardPreview() {
	ScanLocationCard(
		isActive = true,
		holdLocationState = GenericViewState.Idle,
		stepNumber = "3"
	)
}
