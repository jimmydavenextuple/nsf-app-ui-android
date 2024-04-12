package com.nextuple.nsf.ui.screen.prep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.R.drawable
import com.nextuple.nsf.R.string
import com.nextuple.nsf.ui.common.Tab
import com.nextuple.nsf.ui.component.EmptyStateScreen
import com.nextuple.nsf.ui.component.EnterUpcDialog
import com.nextuple.nsf.ui.screen.prep.PrepScreenTab.PACK
import com.nextuple.nsf.ui.screen.prep.PrepScreenTab.STAGE
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.theme.FontFamily
import com.nextuple.nsf.ui.util.NoOpScanManager
import com.nextuple.nsf.ui.util.PreviewPdt
import com.nextuple.nsf.ui.util.ScanManager

@Composable
fun PrepLandingScreen(
    numPackTasks: Int,
    scanManager: ScanManager,
    onScanGear: (String) -> Unit,
    onClickPackByOrder: () -> Unit,
    resetScreen: () -> Unit
) {
	var selectedTab by rememberSaveable { mutableStateOf(PACK) }

	// While this should be a disposable effect, refreshing Prep tab can result in a direct
	// re-composition of this exact screen which does not result in a new set call.
	LaunchedEffect(Unit) {
		scanManager.set { upc, _ -> onScanGear(upc) }
	}

	Column {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(40.dp)
		) {
			Tab(
				modifier = Modifier.weight(1f),
				title = PACK.displayName,
				count = numPackTasks,
				isSelected = selectedTab == PACK
			) {
				selectedTab = PACK
				resetScreen()
			}
			Tab(
				modifier = Modifier.weight(1f),
				title = STAGE.displayName,
				count = 0, // TODO: Undo hardcoding when implemented
				isSelected = selectedTab == STAGE
			) {
				selectedTab = STAGE
				resetScreen()
			}
		}
		if (selectedTab == PACK) {
			PackTabContainer(
				onClickPackByOrder = onClickPackByOrder,
				onScanGear = onScanGear
			)
		} else {
			StageTabContainer()
		}
	}
}

@Composable
private fun PackTabContainer(
	onClickPackByOrder: () -> Unit = {},
	onScanGear: (String) -> Unit

) {
	var showTestScan by remember { mutableStateOf(false) }
	if (showTestScan) {
		EnterUpcDialog(onDismissRequest = { showTestScan = false }, onUpcSubmit = onScanGear)
	}
	Box(
		modifier = Modifier
			.fillMaxHeight()
			.background(BrandColor.WHITE),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Image(
				painter = painterResource(id = drawable.scan_box),
				contentDescription = ""
			)

			Spacer(modifier = Modifier.height(16.dp))

			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(horizontal = 68.dp)
					.clickable {
						if (BuildConfig.DEBUG && BuildConfig.FLAVOR.lowercase() != "prod") {
							showTestScan = true
						}
					}
					.fillMaxWidth()
			) {
				Image(
					painter = painterResource(id = drawable.ic_scan_orange),
					contentDescription = "",
					modifier = Modifier
						.width(36.dp)
						.height(32.dp)
				)
				Text(
					text = stringResource(string.scan_to_pack),
					color = BrandColor.ORANGE_700,
					style = TextStyle(
						fontSize = 18.sp,
						lineHeight = 23.4.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(700),
						letterSpacing = 0.5.sp
					)
				)
			}

			Spacer(modifier = Modifier.height(60.dp))

			Row(modifier = Modifier.padding(horizontal = 56.dp)) {
				ClickableText(
					text = AnnotatedString(stringResource(string.pack_by_order)),
					style = TextStyle(
						fontSize = 14.sp,
						fontFamily = FontFamily.ARCHIVO,
						fontWeight = FontWeight(700),
						color = BrandColor.GRAY_900,
						textAlign = TextAlign.Center,
						letterSpacing = 1.5.sp,
						textDecoration = TextDecoration.Underline
					),
					onClick = {
						onClickPackByOrder()
					}
				)
			}
		}
	}
}

@Composable
private fun StageTabContainer() {
	Box(
		modifier = Modifier
			.fillMaxHeight()
			.background(BrandColor.WHITE),
		contentAlignment = Alignment.Center
	) {
		EmptyStateScreen(
			title = stringResource(id = string.prep_under_construction_title),
			body = stringResource(id = string.prep_under_construction_body),
			imageVector = ImageVector.vectorResource(id = drawable.under_construction)
		)
	}
}

@Composable
@PreviewPdt
fun PreviewPrepLandingScreen() {
	PrepLandingScreen(
		numPackTasks = 0,
		scanManager = NoOpScanManager(),
		onScanGear = {},
		onClickPackByOrder = {}
	) {}
}
