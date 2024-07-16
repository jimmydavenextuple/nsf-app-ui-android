package com.nextuple.nsf.ui.common

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.theme.BrandColor

@Composable
fun ScanIcon(
	modifier: Modifier = Modifier,
	isScanned: Boolean
) {
	if (isScanned) {
		Icon(
			modifier = modifier,
			imageVector = ImageVector.vectorResource(R.drawable.ic_check),
			contentDescription = "scan success icon",
			tint = BrandColor.DARK_BLUE
		)
	} else {
		Icon(
			modifier = modifier,
			imageVector = ImageVector.vectorResource(R.drawable.ic_pick_qr_code_scanner),
			contentDescription = "scan icon",
			tint = BrandColor.GRAY_400
		)
	}
}
