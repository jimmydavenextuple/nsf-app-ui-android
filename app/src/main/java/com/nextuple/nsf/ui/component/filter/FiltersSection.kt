package com.nextuple.nsf.ui.component.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextuple.nsf.ui.common.chip.CheckableChip
import com.nextuple.nsf.ui.theme.BrandColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersSection(
	modifier: Modifier = Modifier,
	title: String,
	filters: List<Filter>,
	valueToFilterContent: @Composable (filter: Filter) -> Unit
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(6.dp)
	) {
		Text(
			text = title,
			color = BrandColor.GRAY_700,
			fontWeight = FontWeight(700),
			fontSize = 12.sp,
			letterSpacing = 1.2.sp
		)
		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			filters.forEach {
				valueToFilterContent(it)
			}
		}
	}
}

private val PREVIEW_WIDTH = 250.dp

@Composable
@Preview(showBackground = true)
fun PreviewFiltersSection() {
	FiltersSection(
		modifier = Modifier.width(PREVIEW_WIDTH),
		title = "Fulfillment Type",
		filters = listOf(
			Filter("bopis"),
			Filter("bopl"),
			Filter("sdd")
		)
	) {
		CheckableChip(text = it.text, isChecked = it.isSelected)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewFiltersSectionChecked() {
	FiltersSection(
		modifier = Modifier.width(PREVIEW_WIDTH),
		title = "Fulfillment Type",
		filters = listOf(
			Filter("bopis"),
			Filter("bopl", isSelected = true),
			Filter("sdd")
		)
	) {
		CheckableChip(text = it.text, isChecked = it.isSelected)
	}
}
