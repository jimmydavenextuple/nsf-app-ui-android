package com.nextuple.nsf.ui.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.service.OrderService
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle,
	private val orderService: OrderService
) : ViewModel() {

	var viewState: GenericViewState by mutableStateOf(GenericViewState.Loading)
		private set

	var completeOrderPickupState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var orderDetailsState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var startPickupState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	var errMsg: String? by mutableStateOf(null)
		private set

	var orderDetailResponse: OrderDetailsResponse? by mutableStateOf(null)
		private set

	var orderList: List<OrderDetailsResponse>? by mutableStateOf(null)
		private set

	var pickUpOrderList = derivedStateOf { orderList }

	fun getOrders(query: String? = null, pastDays: String? = null, dks: String) = viewModelScope.launch {
		viewState = GenericViewState.Loading
		orderList = when (val res = orderService.getOrders(query = query, pastDays = pastDays, dks = dks)) {
			is Result.Success -> {
				errMsg = null
				viewState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				viewState = GenericViewState.Failure
				null
			}
		}
	}

	fun getOrderDetails(fulfillmentRequestNumber: String, dks: String) = viewModelScope.launch {
		orderDetailsState = GenericViewState.Loading
		orderDetailResponse = when (val res = orderService.getOrderDetails(fulfillmentRequestNumber, dks)) {
			is Result.Success -> {
				errMsg = null
				orderDetailsState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				orderDetailsState = GenericViewState.Failure
				null
			}
		}
	}

	fun pickupExtend(dks: String, fulfillmentRequestNumber: String) = viewModelScope.launch {
		viewState = GenericViewState.Loading
		orderDetailResponse = when (val res = orderService.pickupExtend(dks = dks, fulfillmentRequestNumber = fulfillmentRequestNumber)) {
			is Result.Success -> {
				errMsg = null
				viewState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				viewState = GenericViewState.Failure
				null
			}
		}
	}

	fun pickupRemoveCheckIn(dks: String, taskId: String) = viewModelScope.launch {
		viewState = GenericViewState.Loading
		orderDetailResponse = when (val res = orderService.pickupRemoveCheckIn(dks = dks, taskId = taskId)) {
			is Result.Success -> {
				errMsg = null
				viewState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				viewState = GenericViewState.Failure
				null
			}
		}
	}

	fun startPickup(dks: String, fulfillmentRequestNumber: String) = viewModelScope.launch {
		startPickupState = GenericViewState.Loading
		orderDetailResponse = when (val res = orderService.startPickupTask(dks = dks, fulfillmentRequestNumber = fulfillmentRequestNumber)) {
			is Result.Success -> {
				errMsg = null
				startPickupState = GenericViewState.Success
				res.data
			}

			is Result.Error -> {
				errMsg = res.msg
				startPickupState = GenericViewState.Failure
				null
			}
		}
	}

	fun completePickupTask(dks: String, taskId: String) = viewModelScope.launch {
		completeOrderPickupState = GenericViewState.Loading
		orderDetailResponse =
			when (val res = orderService.completePickupTask(dks = dks, taskId = taskId)) {
				is Result.Success -> {
					errMsg = null
					completeOrderPickupState = GenericViewState.Success
					res.data
				}

				is Result.Error -> {
					errMsg = res.msg
					completeOrderPickupState = GenericViewState.Failure
					null
				}
			}
	}

	/**
	 * Removing existing state and default to default state.
	 */
	fun resetCompleteOrderPickState() {
		completeOrderPickupState = GenericViewState.Idle
	}

	/**
	 * Removing existing orderDetailsState state and default to default state.
	 */
	fun resetOrderDetailsState() {
		orderDetailsState = GenericViewState.Idle
	}

	/**
	 * Removing existing startPickupState state and default to default state.
	 */
	fun resetStarPickupState() {
		startPickupState = GenericViewState.Idle
	}
}
