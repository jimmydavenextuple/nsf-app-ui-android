package com.nextuple.nsf.ui.component.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.R
import com.nextuple.nsf.ui.common.TertiaryButton
import com.nextuple.nsf.ui.common.chip.OverflowChip
import com.nextuple.nsf.ui.common.chip.RemovableChip
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.conditional

private const val OVERFLOW_CHAR_COUNT = 20

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersHeader(
	modifier: Modifier = Modifier,
	selectedFilters: List<String>,
	onRemoveFilter: (removed: String) -> Unit,
	onStartFiltersSelection: () -> Unit
) {
	val hasNoSelection = selectedFilters.isEmpty()
	val hasOverflow = selectedFilters.sumOf { it.count() } > OVERFLOW_CHAR_COUNT

	Box(
		modifier = modifier
			.fillMaxWidth()
			.padding(8.dp)
	) {
		FlowRow(
			modifier = Modifier.conditional(
				condition = hasOverflow,
				onTrue = { align(Alignment.CenterStart) },
				onFalse = { align(Alignment.CenterEnd) }
			),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			horizontalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Icon(
				modifier = Modifier
					.size(24.dp)
					.align(Alignment.CenterVertically)
					.clickable { onStartFiltersSelection() },
				imageVector = ImageVector.vectorResource(id = R.drawable.ic_filters),
				tint = if (hasNoSelection) BrandColor.GRAY_900 else BrandColor.BLUE_300_NT,
				contentDescription = "Select Filters"
			)
			if (hasNoSelection) {
				TertiaryButton(
					modifier = Modifier.align(Alignment.CenterVertically),
					text = stringResource(id = R.string.filters).uppercase(),
					onButtonClick = { onStartFiltersSelection() }
				)
			} else {
				var charCount = 0
				var filterCount = 0
				selectedFilters.takeWhile {
					charCount += it.count()
					charCount < OVERFLOW_CHAR_COUNT
				}.forEach {
					filterCount += 1

					RemovableChip(
						modifier = Modifier.align(Alignment.CenterVertically),
						text = it.uppercase()
					) {
						onRemoveFilter(it)
					}
				}
				if (hasOverflow) {
					OverflowChip(
						modifier = Modifier
							.align(Alignment.CenterVertically)
							.clickable {
								onStartFiltersSelection()
							},
						overflowCount = selectedFilters.size - filterCount
					)
				}
			}
		}
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewFiltersHeader() {
	FiltersHeader(selectedFilters = emptyList(), onRemoveFilter = {}) {}
}

@Composable
@Preview(showBackground = true)
fun PreviewFiltersHeaderSelected() {
	FiltersHeader(
		selectedFilters = listOf(
			"bopis",
			"bopl",
			"pack"
		),
		onRemoveFilter = {}
	) {}
}

@Composable
@Preview(showBackground = true)
fun PreviewFiltersHeaderSelectedOverflow() {
	FiltersHeader(
		selectedFilters = listOf(
			"bopis",
			"bopl",
			"sdd",
			"completed",
			"canceled"
		),
		onRemoveFilter = {}
	) {}
}
