package com.nextuple.nsf.ui.component.filter

import com.nextuple.nsf.ui.component.filter.Filter.Companion.allFilter

data class Filter(val text: String, val isSelected: Boolean = false) {

	companion object {
		private const val ALL_FILTER_TEXT = "all"

		fun allFilter(isSelected: Boolean = false): Filter = Filter(
			text = ALL_FILTER_TEXT,
			isSelected = isSelected
		)
	}

	fun isAllFilter(): Boolean = text.equals(ALL_FILTER_TEXT, ignoreCase = true)
}

fun List<Filter>.prependAllFilter(): List<Filter> =
	listOf(allFilter(isSelected = none { it.isSelected })) + this

fun List<Filter>.toSelectedValues(): List<String> =
	filter { it.isSelected }.map { it.text }.distinct()

fun List<Filter>.toUpdated(filter: Filter): List<Filter> = map {
	if (it.text.equals(filter.text, ignoreCase = true)) filter.copy() else it
}

fun List<Filter>.toUpdatedWithAll(filter: Filter): List<Filter> =
	if (filter.isAllFilter() && filter.isSelected) {
		map { it.copy(isSelected = false) }
	} else {
		toUpdated(filter)
	}.filter { !it.isAllFilter() }
