package com.nextuple.nsf.service

import com.nextuple.nsf.retrofit.api.OrderApi
import com.nextuple.nsf.retrofit.dto.response.OrderDetailsResponse
import com.nextuple.nsf.service.dto.Result

class OrderService(private val orderApi: OrderApi, private val deviceService: DeviceService) {

	suspend fun getOrders(query: String?, pastDays: String?, dks: String): Result<List<OrderDetailsResponse>> {
		val store = deviceService.getStore()
		val res = orderApi.getOrders(
			store = store.id,
			query = query,
			pastDays = pastDays,
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

	suspend fun getOrderDetails(fulfillmentRequestNumber: String, dks: String): Result<OrderDetailsResponse> {
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

	suspend fun pickupExtend(dks: String, fulfillmentRequestNumber: String): Result<OrderDetailsResponse> {
		val res = orderApi.pickupExtend(userId = dks, fulfillmentRequestNumber = fulfillmentRequestNumber)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun pickupRemoveCheckIn(dks: String, taskId: String): Result<OrderDetailsResponse> {
		val res = orderApi.pickupRemoveCheckIn(userId = dks, taskId = taskId)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun startPickupTask(dks: String, fulfillmentRequestNumber: String): Result<OrderDetailsResponse> {
		val res = orderApi.startPickupTask(userId = dks, fulfillmentRequestNumber = fulfillmentRequestNumber)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun completePickupTask(dks: String, taskId: String): Result<OrderDetailsResponse> {
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
