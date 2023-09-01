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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
			.height(APP_NAV_BAR_HEIGHT_DP.dp)
			.background(BrandColor.GRAY_200)
			.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		val interactionSource = remember { MutableInteractionSource() }

		items.forEachIndexed { index, item ->
			AddItem(
				item = item.copy(count = tasksUnassigned.getOrNull(index) ?: 0),
				isSelected = isSelected,
				onSelect = onSelect,
				interactionSource = interactionSource
			)
		}
	}
}

@Composable
fun RowScope.AddItem(
	item: AppNavBarItem,
	isSelected: (route: String) -> Boolean,
	onSelect: (route: String) -> Unit,
	interactionSource: MutableInteractionSource
) {
	val isRouteSelected = isSelected(item.route)

	Column(
		modifier = Modifier
			.fillMaxHeight()
			.weight(1f)
			.testTag("navigationBarItem_${item.label}")
			.clickable(
				interactionSource = interactionSource,
				indication = null
			) {
				onSelect(item.route)
			}.let { modifier ->
				if (isRouteSelected) {
					modifier
						.background(color = BrandColor.GRAY_50)
				} else {
					modifier
				}
			},
		verticalArrangement = Arrangement.Center
	) {
		Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
			Icon(
				modifier = Modifier
					.testTag("navigationBarItemIcon_${item.label}")
					.padding(start = 16.dp, top = 9.dp, end = 16.dp)
					.size(21.dp),
				imageVector = ImageVector.vectorResource(id = item.iconResId),
				tint = if (isRouteSelected) BrandColor.GREEN_500 else BrandColor.GRAY_900,
				contentDescription = "Navigate to ${item.label}"
			)

			if (item.count > 0) {
				Box(
					modifier = Modifier
						.size(21.dp)
						.background(BrandColor.ORANGE_600, CircleShape)
						.align(Alignment.TopEnd)
						.padding(bottom = 1.dp),
					contentAlignment = Alignment.Center,
					content = {
						Text(
							text = if (item.count > 99) stringResource(id = R.string.ninety_nine_plus) else item.count.toString(),
							color = BrandColor.GRAY_50,
							fontSize = if (item.count < 9) 15.sp else if (item.count in 10..99) 12.sp else 10.sp,
							fontWeight = FontWeight.Bold
						)
					}
				)
			}
		}
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			modifier = Modifier
				.testTag("navigationBarItemLabel_${item.label}")
				.align(Alignment.CenterHorizontally),
			text = item.label.uppercase(),
			color = if (isRouteSelected) BrandColor.GREEN_500 else BrandColor.GRAY_900,
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
		isSelected = { route -> route == Route.HOME },
		onSelect = {},
		tasksUnassigned = listOf(0, 0, 0, 0)
	)
}
