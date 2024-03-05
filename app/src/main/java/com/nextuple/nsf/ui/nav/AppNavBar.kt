package com.nextuple.nsf.ui.nav

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.AppConfig.NAV_ITEMS
import com.nextuple.nsf.ui.theme.BrandColor

const val APP_NAV_BAR_HEIGHT_DP = 57

data class AppNavBarItem(
	@DrawableRes
	val iconResId: Int,
	val label: String,
	val route: String,
	val count: Int
)

@Composable
fun AppNavBar(
    items: List<AppNavBarItem>,
    isSelected: (route: String) -> Boolean,
    onSelect: (route: String) -> Unit,
    tasksUnassigned: List<Int>
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(APP_NAV_BAR_HEIGHT_DP.dp)
			.background(BrandColor.GRAY_200),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		val interactionSource = remember { MutableInteractionSource() }

		items.forEachIndexed { i, item ->
			AppNavItem(
				item = item.copy(count = tasksUnassigned.getOrNull(i) ?: 0),
				isSelected = isSelected,
				onSelect = onSelect,
				interactionSource = interactionSource
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.AppNavItem(
    item: AppNavBarItem,
    isSelected: (route: String) -> Boolean,
    onSelect: (route: String) -> Unit,
    interactionSource: MutableInteractionSource
) {
	val isRouteSelected = isSelected(item.route)

	Column(
		modifier = Modifier
			.testTag("navigationBarItem_${item.label}")
			.weight(1f)
			.fillMaxSize()
			.clickable(
				interactionSource = interactionSource,
				indication = null
			) {
				onSelect(item.route)
			}.let {
				if (isRouteSelected) it.background(color = BrandColor.GRAY_50) else it
			},
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		BadgedBox(
			badge = {
				if (item.count > 0) {
					Box(
						modifier = Modifier
							.size(20.dp)
							.background(BrandColor.PINK_NT, CircleShape),
						contentAlignment = Alignment.Center,
						content = {
							Text(
								text = if (item.count > 99) stringResource(id = R.string.ninety_nine_plus) else item.count.toString(),
								color = BrandColor.GRAY_50,
								fontSize = if (item.count < 9) 14.sp else if (item.count in 10..99) 12.sp else 10.sp,
								fontWeight = FontWeight.Bold
							)
						}
					)
				}
			}
		) {
			Icon(
				modifier = Modifier
					.testTag("navigationBarItemIcon_${item.label}")
					.size(20.dp)
					.align(Alignment.Center),
				imageVector = ImageVector.vectorResource(id = item.iconResId),
				tint = if (isRouteSelected) BrandColor.BLUE_300_NT else BrandColor.GRAY_900,
				contentDescription = "Navigate to ${item.label}"
			)
		}
		Spacer(modifier = Modifier.height(2.dp))
		Text(
			modifier = Modifier
				.testTag("navigationBarItemLabel_${item.label}"),
			text = item.label.uppercase(),
			color = if (isRouteSelected) BrandColor.BLUE_300_NT else BrandColor.GRAY_900,
			fontSize = if (isRouteSelected) 12.sp else 10.sp,
			fontWeight = FontWeight(700),
			letterSpacing = 1.5.sp
		)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewAppNavBar() {
	AppNavBar(
		items = NAV_ITEMS,
		isSelected = { route -> route == Screen.HOME.route },
		onSelect = {},
		tasksUnassigned = listOf(0, 3, 100, 0)
	)
}
