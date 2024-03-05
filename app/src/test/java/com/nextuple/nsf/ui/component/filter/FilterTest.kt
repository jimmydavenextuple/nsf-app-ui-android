package com.nextuple.nsf.ui.component.filter

import com.nextuple.nsf.ui.component.filter.Filter.Companion.allFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterTest {

	@Test
	fun `isAllFilter should return true if the filter is all`() {
		assertTrue(allFilter().isAllFilter())
	}

	@Test
	fun `isAllFilter should return false if the filter is not all`() {
		val notAllFilter = allFilter().let { it.copy(text = it.text + "lala") }
		assertFalse(notAllFilter.isAllFilter())
	}

	@Test
	fun `prependAllFilter should prepend false when there are selected non-ALL filters`() {
		val aFilter = Filter(text = "a", isSelected = true)
		val expected = listOf(allFilter(isSelected = false)) + listOf(aFilter)
		assertEquals(expected, listOf(aFilter).prependAllFilter())
	}

	@Test
	fun `prependAllFilter should prepend true when there are no selected non-ALL filters`() {
		val aFilter = Filter(text = "a", isSelected = false)
		val expected = listOf(allFilter(isSelected = true)) + listOf(aFilter)
		assertEquals(expected, listOf(aFilter).prependAllFilter())
	}

	@Test
	fun `toSelectedValues should return distinct values preserving first appearance order`() {
		val selectedFilters = listOf("a", "b", "b", "a", "c").map { Filter(it, true) }
		val notSelectedFilter = Filter("d", false)
		val filters = selectedFilters.plus(notSelectedFilter)
		assertEquals(listOf("a", "b", "c"), filters.toSelectedValues())
	}

	@Test
	fun `toUpdated should correctly update the list with the provided filter`() {
		val allSelected = listOf(Filter("a", true), Filter("b", true))
		val noneSelected = listOf(Filter("a", false), Filter("b", false))
		assertEquals(
			allSelected,
			noneSelected
				.toUpdated(Filter("b", true))
				.toUpdated(Filter("a", true))
		)
	}

	@Test
	fun `toUpdated should ignore filters that do not match any filters`() {
		val allSelected = listOf(Filter("a", true), Filter("b", true))
		assertEquals(
			allSelected,
			allSelected
				.toUpdated(Filter("c", false))
				.toUpdated(Filter("d", false))
		)
	}
}
