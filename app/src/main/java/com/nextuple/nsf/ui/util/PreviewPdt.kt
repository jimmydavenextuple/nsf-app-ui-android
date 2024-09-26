package com.nextuple.nsf.ui.util

import androidx.compose.ui.tooling.preview.Preview
import com.nextuple.nsf.ui.nav.APP_NAV_BAR_HEIGHT_DP

private const val SYSTEM_UI_HEIGHT_DELTA_DP = 72
private const val APP_TOP_BAR_HEIGHT_DP = 48

@Preview(
	name = "PDT",
	widthDp = 360,
	heightDp = 640 - SYSTEM_UI_HEIGHT_DELTA_DP - APP_TOP_BAR_HEIGHT_DP - APP_NAV_BAR_HEIGHT_DP,
	showBackground = true
)
annotation class PreviewPdt

@Preview(
	name = "PDT",
	widthDp = 360,
	heightDp = 800 - SYSTEM_UI_HEIGHT_DELTA_DP - APP_TOP_BAR_HEIGHT_DP - APP_NAV_BAR_HEIGHT_DP,
	showBackground = true
)
annotation class PreviewPdtNordOne

@Preview(
	name = "PDT_WIDTH",
	widthDp = 360,
	showBackground = true
)
annotation class PreviewPdtWidth
