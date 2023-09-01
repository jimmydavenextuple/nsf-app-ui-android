package com.nextuple.nsf.service

import com.nextuple.nsf.retrofit.api.PickApi
import com.nextuple.nsf.retrofit.dto.DeclineItemRequest
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.util.FulfillmentType
import com.nextuple.nsf.util.SubFulfillmentType

class PickService(
	private val pickApi: PickApi,
	private val deviceService: DeviceService
) {
	suspend fun startPick(dks: String): Result<PickTask?> {
		val store = deviceService.getStore()
		println("Logging in for brand ${store.brand} store ${store.id}")

		val res = pickApi.startPick(store = store.id, userId = dks)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun declinePick(declineItemRequest: DeclineItemRequest, dks: String): Result<PickTask?> {
		val res = pickApi.pickDecline(declineItemRequest = declineItemRequest, userId = dks)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun pickItem(pickItemRequest: PickItemRequest, dks: String): Result<PickTask?> {
		val res = pickApi.recordPick(pickItemRequest = pickItemRequest, userId = dks)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun getDeclineCodes(dks: String): Result<GetDeclineCodesResponse> {
		val store = deviceService.getStore()
		val res = pickApi.getDeclineCodes(
			brand = store.brand.name,
			store = store.id,
			fulfillmentType = FulfillmentType.BOPIS.name,
			subFulfillmentType = SubFulfillmentType.BOPIS.name,
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
}
