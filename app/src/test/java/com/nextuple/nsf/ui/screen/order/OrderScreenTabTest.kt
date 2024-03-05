package com.nextuple.nsf.ui.screen.order

import com.nextuple.nsf.ui.screen.order.OrderScreenTab.Companion.determineSelectedTab
import com.nextuple.nsf.ui.screen.order.OrderScreenTab.IN_PROGRESS
import com.nextuple.nsf.ui.screen.order.OrderScreenTab.READY
import org.junit.Assert.assertEquals
import org.junit.Test

class OrderScreenTabTest {
	companion object {
		// These should match one of OrderViewModel filter values of the right category.
		private const val READY_FILTER_VALUE = "ready"
		private const val IN_PROGRESS_FILTER_VALUE = "pack"
	}

	@Test
	fun `determineSelectedTab, when only filters relevant to current tab, should stay`() {
		assertEquals(
			READY,
			determineSelectedTab(
				current = READY,
				hasReadyOrders = true,
				hasInProgressOrders = true,
				selectedFilters = listOf(READY_FILTER_VALUE)
			)
		)
		assertEquals(
			IN_PROGRESS,
			determineSelectedTab(
				current = IN_PROGRESS,
				hasReadyOrders = true,
				hasInProgressOrders = true,
				selectedFilters = listOf(IN_PROGRESS_FILTER_VALUE)
			)
		)
	}

	@Test
	fun `determineSelectedTab, when only filters relevant to other tab, should go`() {
		assertEquals(
			READY,
			determineSelectedTab(
				current = IN_PROGRESS,
				hasReadyOrders = true,
				hasInProgressOrders = true,
				selectedFilters = listOf(READY_FILTER_VALUE)
			)
		)
		assertEquals(
			IN_PROGRESS,
			determineSelectedTab(
				current = READY,
				hasReadyOrders = true,
				hasInProgressOrders = true,
				selectedFilters = listOf(IN_PROGRESS_FILTER_VALUE)
			)
		)
	}

	@Test
	fun `determineSelectedTab, when filters relevant to both tabs, but both has no results, should stay`() {
		assertEquals(
			READY,
			determineSelectedTab(
				current = READY,
				hasReadyOrders = false,
				hasInProgressOrders = false,
				selectedFilters = listOf(READY_FILTER_VALUE, IN_PROGRESS_FILTER_VALUE)
			)
		)
		assertEquals(
			IN_PROGRESS,
			determineSelectedTab(
				current = IN_PROGRESS,
				hasReadyOrders = false,
				hasInProgressOrders = false,
				selectedFilters = listOf(READY_FILTER_VALUE, IN_PROGRESS_FILTER_VALUE)
			)
		)
	}

	@Test
	fun `determineSelectedTab, when filters relevant to both tabs, but current has results, should stay`() {
		assertEquals(
			READY,
			determineSelectedTab(
				current = READY,
				hasReadyOrders = true,
				hasInProgressOrders = true,
				selectedFilters = listOf(READY_FILTER_VALUE, IN_PROGRESS_FILTER_VALUE)
			)
		)
		assertEquals(
			IN_PROGRESS,
			determineSelectedTab(
				current = IN_PROGRESS,
				hasReadyOrders = true,
				hasInProgressOrders = true,
				selectedFilters = listOf(READY_FILTER_VALUE, IN_PROGRESS_FILTER_VALUE)
			)
		)
	}

	@Test
	fun `determineSelectedTab, when filters relevant to both tabs, but current has no results, should go`() {
		assertEquals(
			READY,
			determineSelectedTab(
				current = IN_PROGRESS,
				hasReadyOrders = true,
				hasInProgressOrders = false,
				selectedFilters = listOf(READY_FILTER_VALUE, IN_PROGRESS_FILTER_VALUE)
			)
		)
		assertEquals(
			IN_PROGRESS,
			determineSelectedTab(
				current = READY,
				hasReadyOrders = false,
				hasInProgressOrders = true,
				selectedFilters = listOf(READY_FILTER_VALUE, IN_PROGRESS_FILTER_VALUE)
			)
		)
	}
}
