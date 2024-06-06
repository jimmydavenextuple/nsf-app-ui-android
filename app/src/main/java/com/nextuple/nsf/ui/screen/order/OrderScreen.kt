package com.nextuple.nsf.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.nextuple.nsf.R
import com.nextuple.nsf.retrofit.dto.response.FulfillmentRequestDetail
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.ui.common.Tab
import com.nextuple.nsf.ui.component.EmptyStateScreen
import com.nextuple.nsf.ui.component.filter.Filter
import com.nextuple.nsf.ui.component.filter.FiltersBottomSheet
import com.nextuple.nsf.ui.component.filter.FiltersHeader
import com.nextuple.nsf.ui.component.filter.toSelectedValues
import com.nextuple.nsf.ui.component.filter.toUpdated
import com.nextuple.nsf.ui.theme.BrandColor
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.PreviewPdt
import kotlinx.coroutines.launch

private val FILTERS_BOTTOM_INSET_VISIBLE = 120.dp
private val FILTERS_BOTTOM_INSET_HIDDEN = 0.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
	viewState: GenericViewState = GenericViewState.Loading,
	orderDetailsState: GenericViewState = GenericViewState.Idle,
	getOrders: (query: String?) -> Unit,
	getOrderDetails: (String) -> Unit,
	getOrderDetailsCompletion: () -> Unit,
	readyOrders: List<OrderDetailsResponse>,
	sddReadyOrder: List<List<OrderDetailsResponse>>? = null,
	inProgressOrders: List<OrderDetailsResponse>,
	orderTypeFilters: List<Filter>,
	orderStatusFilters: List<Filter>,
	selectedTab: OrderScreenTab,
	onSelectTab: (tab: OrderScreenTab) -> Unit,
	onApplyFilters: (orderTypeFilters: List<Filter>, orderStatusFilters: List<Filter>) -> Unit
) {
	val scope = rememberCoroutineScope()

	var showFilters by rememberSaveable { mutableStateOf(false) }
	var filtersBottomInset =
		if (showFilters) FILTERS_BOTTOM_INSET_VISIBLE else FILTERS_BOTTOM_INSET_HIDDEN

	val filtersState = rememberModalBottomSheetState()
	val onShowFilters = {
		filtersBottomInset = FILTERS_BOTTOM_INSET_VISIBLE
		scope.launch {
			showFilters = true
			filtersState.show()
		}
	}
	val onDismissFilters = {
		scope.launch {
			filtersState.hide()
			showFilters = false
		}
		filtersBottomInset = FILTERS_BOTTOM_INSET_HIDDEN
	}

	LaunchedEffect(Unit) {
		getOrders(null)
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = BrandColor.GRAY_50)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.height(40.dp)
			) {
				Tab(
					modifier = Modifier.weight(1f),
					title = OrderScreenTab.READY.displayName,
					count = readyOrders.size + (sddReadyOrder?.size ?: 0),
					isSelected = selectedTab == OrderScreenTab.READY
				) {
					onSelectTab(OrderScreenTab.READY)
					getOrders(null)
				}
				Tab(
					modifier = Modifier.weight(1f),
					title = OrderScreenTab.IN_PROGRESS.displayName,
					count = inProgressOrders.size,
					isSelected = selectedTab == OrderScreenTab.IN_PROGRESS
				) {
					onSelectTab(OrderScreenTab.IN_PROGRESS)
					getOrders(null)
				}
			}
			FiltersHeader(
				selectedFilters = orderTypeFilters.toSelectedValues() + orderStatusFilters.toSelectedValues(),
				onRemoveFilter = {
					val newFilter = Filter(text = it, isSelected = false)
					onApplyFilters(
						orderTypeFilters.toUpdated(newFilter),
						orderStatusFilters.toUpdated(newFilter)
					)
					getOrders(null)
				}
			) {
				onShowFilters()
			}
			if (readyOrders.isEmpty() && sddReadyOrder.isNullOrEmpty() && selectedTab == OrderScreenTab.READY) {
				EmptyStateScreen(
					title = stringResource(R.string.empty_ready_title),
					body = stringResource(R.string.empty_ready_body),
					imageVector = ImageVector.vectorResource(id = R.drawable.ic_stop_watch)
				)
			} else if (inProgressOrders.isEmpty() && selectedTab == OrderScreenTab.IN_PROGRESS) {
				EmptyStateScreen(
					title = stringResource(R.string.empty_in_progress_title),
					body = stringResource(R.string.empty_in_progress_body),
					imageVector = ImageVector.vectorResource(id = R.drawable.ic_basketball_hoop)
				)
			}
			OrderCardList(
				modifier = Modifier.padding(top = 10.dp),
				orderList = if (selectedTab == OrderScreenTab.READY) readyOrders else inProgressOrders,
				getOrderDetails = getOrderDetails,
				sddReadyList = sddReadyOrder
			)
		}
		if (viewState == GenericViewState.Loading || orderDetailsState == GenericViewState.Loading) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center),
				color = BrandColor.GRAY_900
			)
		} else if (orderDetailsState == GenericViewState.Success) {
			getOrderDetailsCompletion.invoke()
		}
	}

	if (showFilters) {
		FiltersBottomSheet(
			state = filtersState,
			bottomInset = filtersBottomInset,
			initialOrderTypes = orderTypeFilters,
			initialOrderStatuses = orderStatusFilters,
			onDismiss = { onDismissFilters() }
		) { updatedOrderTypeFilters, updatedOrderStatusFilters ->
			onDismissFilters()
			onApplyFilters(updatedOrderTypeFilters, updatedOrderStatusFilters)
			getOrders(null)
		}
	}
}

@Composable
@PreviewPdt
fun PreviewOrderScreen() {
	OrderScreen(
		getOrders = { _ -> },
		getOrderDetails = {},
		getOrderDetailsCompletion = {},
		readyOrders = listOf(
			OrderDetailsResponse(
				fulfillmentRequestDetail = FulfillmentRequestDetail(
					fulfillmentRequestNumber = "100000000123.001",
					fulfillmentType = "BOPIS",
					subFulfillmentType = "BOPL"
				)
			)
		),
		inProgressOrders = emptyList(),
		orderTypeFilters = listOf(
			Filter("bopis"),
			Filter("bopl"),
			Filter("sdd")
		),
		orderStatusFilters = listOf(
			Filter("pack"),
			Filter("stage"),
			Filter("dispense")
		),
		selectedTab = OrderScreenTab.READY,
		onSelectTab = {}
	) { _, _ -> }
}

@Composable
@PreviewPdt
fun PreviewOrderScreen_Swish() {
	OrderScreen(
		getOrders = { _ -> },
		getOrderDetails = {},
		getOrderDetailsCompletion = {},
		readyOrders = emptyList(),
		inProgressOrders = emptyList(),
		orderTypeFilters = listOf(
			Filter("bopis"),
			Filter("bopl"),
			Filter("sdd")
		),
		orderStatusFilters = listOf(
			Filter("pack"),
			Filter("stage"),
			Filter("dispense")
		),
		selectedTab = OrderScreenTab.READY,
		onSelectTab = {}
	) { _, _ -> }
}

@Composable
@PreviewPdt
fun PreviewOrderScreen_inProgressTab() {
	OrderScreen(
		getOrders = { _ -> },
		getOrderDetails = {},
		getOrderDetailsCompletion = {},
		readyOrders = emptyList(),
		inProgressOrders = emptyList(),
		orderTypeFilters = listOf(
			Filter("bopis"),
			Filter("bopl"),
			Filter("sdd")
		),
		orderStatusFilters = listOf(
			Filter("pack"),
			Filter("stage"),
			Filter("dispense")
		),
		selectedTab = OrderScreenTab.IN_PROGRESS,
		onSelectTab = {}
	) { _, _ -> }
}
