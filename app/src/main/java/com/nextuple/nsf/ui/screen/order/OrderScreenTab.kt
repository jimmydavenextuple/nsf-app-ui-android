package com.nextuple.nsf.ui.screen.order

import com.nextuple.nsf.util.OrderStatus

enum class OrderScreenTab(val displayName: String) {
	READY("READY"),
	IN_PROGRESS("IN PROGRESS")
	;

	companion object {
		fun determineSelectedTab(
            current: OrderScreenTab,
            hasReadyOrders: Boolean,
            hasInProgressOrders: Boolean,
            selectedFilters: List<String>
		): OrderScreenTab {
			val hasReadyFilters = selectedFilters.any {
				OrderStatus.isReadyStatus(it) || OrderStatus.isCompleteStatus(it)
			}
			val hasInProgressFilters = selectedFilters.any {
				!OrderStatus.isReadyStatus(it) && !OrderStatus.isCompleteStatus(it)
			}

			return when (current) {
				READY -> {
					val hasPotential = hasInProgressFilters && hasInProgressOrders
					val hasNoReasonToStay = !hasReadyFilters || !hasReadyOrders

					if (hasPotential && hasNoReasonToStay) IN_PROGRESS else READY
				}

				IN_PROGRESS -> {
					val hasPotential = hasReadyFilters && hasReadyOrders
					val hasNoReasonToStay = !hasInProgressFilters || !hasInProgressOrders
					if (hasPotential && hasNoReasonToStay) READY else IN_PROGRESS
				}
			}
		}
	}
}
