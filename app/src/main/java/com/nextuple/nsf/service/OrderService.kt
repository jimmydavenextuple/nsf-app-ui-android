package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.OrderApi
import com.nextuple.nsf.retrofit.dto.RecordDeclineRequest
import com.nextuple.nsf.retrofit.dto.RecordDeclineResponse
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.service.dto.Result

class OrderService(
	private val orderApi: OrderApi,
	private val deviceService: DeviceService,
	private val userRepository: UserRepository
) {

	suspend fun getOrders(
		query: String?,
		orderTypeFilter: List<String>? = null,
		orderStatusFilter: List<String>? = null,
		minOrderStatus: String? = null
	): Result<List<OrderDetailsResponse>> {
		val store = deviceService.getStore() ?: return Result.generalError()
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = orderApi.getOrders(
			store = store.id,
			query = query,
			orderTypeFilter = orderTypeFilter,
			orderStatusFilter = orderStatusFilter,
			minOrderStatus = minOrderStatus,
			pastDays = null,
			userId = dks
		)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun getOrderDetails(fulfillmentRequestNumber: String): Result<OrderDetailsResponse> {
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = orderApi.orderDetails(
			fulfillmentRequestNumber = fulfillmentRequestNumber,
			userId = dks
		)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun recordDecline(
		fulfillmentRequestNumber: String,
		declinedReason: String,
		shouldTranslateReason: Boolean,
		action: String
	): Result<RecordDeclineResponse> {
		val request = RecordDeclineRequest(
			fulfillmentRequestNumber = fulfillmentRequestNumber,
			declinedReason = declinedReason,
			shouldTranslateReason = shouldTranslateReason,
			action = action
		)
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = orderApi.recordDecline(userId = dks, recordDeclineRequest = request)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun pickupExtend(fulfillmentRequestNumber: String): Result<OrderDetailsResponse> {
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res =
			orderApi.pickupExtend(userId = dks, fulfillmentRequestNumber = fulfillmentRequestNumber)

		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun pickupRemoveCheckIn(taskId: String): Result<OrderDetailsResponse> {
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = orderApi.pickupRemoveCheckIn(userId = dks, taskId = taskId)

		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun startPickupTask(fulfillmentRequestNumber: String): Result<OrderDetailsResponse> {
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = orderApi.startPickupTask(
			userId = dks,
			fulfillmentRequestNumber = fulfillmentRequestNumber
		)

		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun completePickupTask(taskId: String): Result<OrderDetailsResponse> {
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = orderApi.completePickupTask(userId = dks, taskId = taskId)

		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}
}
