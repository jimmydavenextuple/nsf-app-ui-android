package com.nextuple.nsf.ui.component.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.R.drawable
import com.nextuple.nsf.R.string
import com.nextuple.nsf.ui.common.PrimaryButton
import com.nextuple.nsf.ui.common.chip.CheckableChip
import com.nextuple.nsf.ui.theme.BrandColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
	state: SheetState,
	bottomInset: Dp = 48.dp,
	initialOrderTypes: List<Filter>,
	initialOrderStatuses: List<Filter>,
	onDismiss: () -> Unit,
	onApplyFilters: (orderTypeFilters: List<Filter>, orderStatusFilters: List<Filter>) -> Unit
) {
	var orderTypes by remember { mutableStateOf(initialOrderTypes) }
	var orderStatuses by remember { mutableStateOf(initialOrderStatuses) }

	ModalBottomSheet(
		sheetState = state,
		shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
		windowInsets = WindowInsets(bottom = bottomInset),
		dragHandle = null,
		onDismissRequest = {
			onDismiss()
			orderTypes = initialOrderTypes
			orderStatuses = initialOrderStatuses
		}
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(start = 18.dp, end = 18.dp, top = 12.dp),
			verticalArrangement = Arrangement.spacedBy(18.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(string.filters),
					color = BrandColor.BLACK,
					style = TextStyle(
						fontSize = 18.sp,
						fontWeight = FontWeight(700)
					)
				)
				Icon(
					modifier = Modifier
						.align(Alignment.CenterVertically)
						.clickable { onDismiss() },
					imageVector = ImageVector.vectorResource(drawable.ic_close),
					tint = BrandColor.GRAY_900,
					contentDescription = "close"
				)
			}
			FiltersSection(
				title = stringResource(string.filters_category_order_type),
				filters = orderTypes.prependAllFilter()
			) {
				CheckableChip(
					modifier = Modifier.clickable {
						orderTypes = orderTypes.toUpdatedWithAll(
							filter = it.copy(isSelected = !it.isSelected)
						)
					},
					text = it.text,
					isChecked = it.isSelected
				)
			}
			FiltersSection(
				title = stringResource(string.filters_category_order_status),
				filters = orderStatuses.prependAllFilter()
			) {
				CheckableChip(
					modifier = Modifier.clickable {
						orderStatuses = orderStatuses.toUpdatedWithAll(
							filter = it.copy(isSelected = !it.isSelected)
						)
					},
					text = it.text,
					isChecked = it.isSelected
				)
			}
			Box(
				modifier = Modifier
					.height(40.dp)
					.fillMaxWidth(),
				contentAlignment = Alignment.Center
			) {
				PrimaryButton(
					modifier = Modifier.fillMaxWidth(),
					text = stringResource(string.apply_filters),
					onButtonClick = {
						onApplyFilters(orderTypes, orderStatuses)
					}
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PreviewFiltersBottomSheet() {
	FiltersBottomSheet(
		state = rememberModalBottomSheetState(),
		initialOrderTypes = listOf(
			Filter("bopis"),
			Filter("bopl"),
			Filter("sdd")
		),
		initialOrderStatuses = listOf(
			Filter("pack"),
			Filter("stage"),
			Filter("dispense")
		),
		onDismiss = {}
	) { _, _ -> }
}
