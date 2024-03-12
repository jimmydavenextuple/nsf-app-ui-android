package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.PickApi
import com.nextuple.nsf.retrofit.dto.DeclineItemRequest
import com.nextuple.nsf.retrofit.dto.PickItemRequest
import com.nextuple.nsf.retrofit.dto.PickTask
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.LogService.Companion.EVENT_PICK_DECLINE
import com.nextuple.nsf.service.LogService.Companion.EVENT_PICK_DECLINE_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_PICK_ITEM
import com.nextuple.nsf.service.LogService.Companion.EVENT_PICK_ITEM_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_PICK_START
import com.nextuple.nsf.service.LogService.Companion.EVENT_PICK_START_RES

class PickService(
    private val pickApi: PickApi,
    private val deviceService: DeviceService,
    private val logService: LogService,
    private val userRepository: UserRepository
) {
	suspend fun startPick(): Result<PickTask?> = runCatching {
		logService.trackEvent(EVENT_PICK_START)

		val store = deviceService.getStore() ?: return Result.generalError()
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = pickApi.startPick(store = store.id, userId = dks)

		return Result.fromApiResponse(res) {
			it!!.also { task ->
				logService.trackEvent(
					EVENT_PICK_START_RES,
					mapOf(
						"taskId" to task.id.toString(),
						"fulfillmentType" to task.fulfillmentType.name,
						"subFulfillmentType" to task.subFulfillmentType.name,
						"qty" to task.totalQty.toString(),
						"onHandInSkuOrder" to task.items.joinToString { item -> item.onHandQty.toString() },
						"skus" to task.items.joinToString { item -> item.sku },
						"upcs" to task.items.flatMap { item -> item.upcs }.joinToString()
					)
				)
			}
		}
	}.onFailure {
		logService.trackError(EVENT_PICK_START, it)
	}.getOrDefault(Result.generalError())

	suspend fun declinePick(
		req: DeclineItemRequest
	): Result<PickTask?> = runCatching {
		logService.trackEvent(
			EVENT_PICK_DECLINE,
			mapOf(
				"taskId" to req.taskId.toString(),
				"sku" to req.sku,
				"declinedQty" to req.declinedQty.toString(),
				"declineReason" to req.declineReason,
				"declineReasonText" to req.declineReasonText,
				"declineRequest" to "${req.taskId} ${req.sku} ${req.declinedQty} ${req.declineReason} ${req.declineReasonText}"
			)
		)

		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = pickApi.pickDecline(declineItemRequest = req, userId = dks)

		return Result.fromApiResponse(res) {
			it!!.also { task ->
				logService.trackEvent(
					EVENT_PICK_DECLINE_RES,
					mapOf(
						"taskId" to req.taskId.toString(),
						"sku" to req.sku,
						"declinedQty" to req.declinedQty.toString(),
						"declineReason" to req.declineReason,
						"declineReasonText" to req.declineReasonText,
						"declineRequest" to "${req.taskId} ${req.sku} ${req.declinedQty} ${req.declineReason} ${req.declineReasonText}",
						"declineResult" to "${req.taskId} ${task.totalQty} ${task.pickedQty} ${task.declinedQty} ${task.totalRemainingQty}"
					)
				)
			}
		}
	}.onFailure {
		logService.trackError(
			EVENT_PICK_DECLINE,
			it,
			mapOf(
				"taskId" to req.taskId.toString(),
				"sku" to req.sku,
				"declinedQty" to req.declinedQty.toString(),
				"declineReason" to req.declineReason,
				"declineReasonText" to req.declineReasonText,
				"declineRequest" to "${req.taskId} ${req.sku} ${req.declinedQty} ${req.declineReason} ${req.declineReasonText}"
			)
		)
	}.getOrDefault(Result.generalError())

	suspend fun pickItem(
		req: PickItemRequest
	): Result<PickTask?> = runCatching {
		logService.trackEvent(
			EVENT_PICK_ITEM,
			mapOf(
				"taskId" to req.taskId.toString(),
				"sku" to req.sku,
				"upc" to req.scannedUpc,
				"pickedQty" to req.pickedQty.toString(),
				"pickedLocation" to req.pickedLocation.toString(),
				"pickItemRequest" to "${req.taskId} ${req.sku} ${req.scannedUpc} ${req.pickedQty} ${req.pickedLocation}"
			)
		)

		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = pickApi.recordPick(pickItemRequest = req, userId = dks)

		return Result.fromApiResponse(res) {
			it!!.also { task ->
				logService.trackEvent(
					EVENT_PICK_ITEM_RES,
					mapOf(
						"taskId" to req.taskId.toString(),
						"sku" to req.sku,
						"upc" to req.scannedUpc,
						"pickedQty" to req.pickedQty.toString(),
						"pickedLocation" to req.pickedLocation.toString(),
						"pickItemRequest" to "${req.taskId} ${req.sku} ${req.scannedUpc} ${req.pickedQty} ${req.pickedLocation}",
						"pickItemResult" to "${req.taskId} ${task.totalQty} ${task.pickedQty} ${task.declinedQty} ${task.totalRemainingQty}"
					)
				)
			}
		}
	}.onFailure {
		logService.trackError(
			EVENT_PICK_ITEM,
			it,
			mapOf(
				"taskId" to req.taskId.toString(),
				"sku" to req.sku,
				"upc" to req.scannedUpc,
				"pickedQty" to req.pickedQty.toString(),
				"pickedLocation" to req.pickedLocation.toString(),
				"pickItemRequest" to "${req.taskId} ${req.sku} ${req.scannedUpc} ${req.pickedQty} ${req.pickedLocation}"
			)
		)
	}.getOrDefault(Result.generalError())
}
