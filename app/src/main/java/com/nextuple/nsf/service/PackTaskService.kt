package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.PackTaskApi
import com.nextuple.nsf.retrofit.dto.GetPrepDetailsRequest
import com.nextuple.nsf.retrofit.dto.PackByGearRequest
import com.nextuple.nsf.retrofit.dto.PackByOrderRequest
import com.nextuple.nsf.retrofit.dto.PrepDetail
import com.nextuple.nsf.retrofit.dto.RecordDeclineRequest
import com.nextuple.nsf.retrofit.dto.RecordDeclineResponse
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.service.LogService.Companion.EVENT_COMPLETE_PACK
import com.nextuple.nsf.service.LogService.Companion.EVENT_COMPLETE_PACK_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_DECLINE_PACK_ITEM
import com.nextuple.nsf.service.LogService.Companion.EVENT_GET_PREP_DETAILS
import com.nextuple.nsf.service.LogService.Companion.EVENT_GET_PREP_DETAILS_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_PACK
import com.nextuple.nsf.service.LogService.Companion.EVENT_PACK_BY_GEAR
import com.nextuple.nsf.service.LogService.Companion.EVENT_PACK_BY_GEAR_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_PACK_BY_ORDER
import com.nextuple.nsf.service.LogService.Companion.EVENT_PACK_BY_ORDER_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_PACK_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_START_PACK_ONLY
import com.nextuple.nsf.service.LogService.Companion.EVENT_START_PACK_ONLY_RES
import com.nextuple.nsf.service.dto.Result

class PackTaskService(
	private val packTaskApi: PackTaskApi,
	private val logService: LogService,
	private val deviceService: DeviceService,
	private val userRepository: UserRepository
) {

	suspend fun getPrepDetails(frNo: String): Result<PrepDetail?> = runCatching {
		logService.trackEvent(EVENT_GET_PREP_DETAILS, mapOf("frNo" to frNo))

		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = packTaskApi.getPrepDetails(
			userId = dks,
			getPrepDetailsRequest = GetPrepDetailsRequest(frNos = listOf(frNo))
		)

		return Result.fromApiResponse(res) {
			it!!.first().also { prepDetail ->
				logService.trackEvent(EVENT_GET_PREP_DETAILS_RES, toPrepDetailsResMap(prepDetail))
			}
		}
	}.onFailure {
		logService.trackError(EVENT_GET_PREP_DETAILS, it, mapOf("frNo" to frNo))
	}.getOrDefault(Result.generalError())

	suspend fun getPrepDetails(frNos: List<String>): Result<List<PrepDetail>?> = runCatching {
		logService.trackEvent(EVENT_GET_PREP_DETAILS, mapOf("frNo" to frNos.joinToString { "," }))

		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = packTaskApi.getPrepDetails(
			userId = dks,
			getPrepDetailsRequest = GetPrepDetailsRequest(frNos = frNos)
		)

		return Result.fromApiResponse(res) {
			it!!.onEach { prepDetail ->
				logService.trackEvent(EVENT_GET_PREP_DETAILS_RES, toPrepDetailsResMap(prepDetail))
			}
		}
	}.onFailure {
		logService.trackError(
			EVENT_GET_PREP_DETAILS,
			it,
			mapOf("frNos" to frNos.joinToString { "," })
		)
	}.getOrDefault(Result.generalError())

	suspend fun packByGear(upc: String): Result<PrepDetail?> = runCatching {
		val store = deviceService.getStore() ?: return Result.generalError()
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = packTaskApi.packByGear(
			userId = dks,
			packByGearRequest = PackByGearRequest(storeNumber = store.id, upc = upc)
		)
		return Result.fromApiResponse(res) {
			it!!.also { prepDetail ->
				logService.trackEvent(EVENT_PACK_BY_GEAR_RES, toPrepDetailsResMap(prepDetail))
			}
		}
	}.onFailure {
		logService.trackError(EVENT_PACK_BY_GEAR, it, mapOf("upc" to upc))
	}.getOrDefault(Result.generalError())

	suspend fun packByOrder(frNo: String): Result<PrepDetail?> = runCatching {
		val store = deviceService.getStore() ?: return Result.generalError()
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = packTaskApi.packByOrder(
			userId = dks,
			packByOrderRequest = PackByOrderRequest(storeNumber = store.id, frNo = frNo)
		)
		return Result.fromApiResponse(res) {
			it!!.also { prepDetail ->
				logService.trackEvent(EVENT_PACK_BY_ORDER_RES, toPrepDetailsResMap(prepDetail))
			}
		}
	}.onFailure {
		logService.trackError(EVENT_PACK_BY_ORDER, it, mapOf("frNo" to frNo))
	}.getOrDefault(Result.generalError())

	suspend fun declinePackItem(req: RecordDeclineRequest): Result<RecordDeclineResponse?> =
		runCatching {
			val dks = userRepository.getDks() ?: return Result.generalError()
			val res = packTaskApi.declinePackItem(
				userId = dks,
				recordDeclineRequest = req
			)
			return Result.fromApiResponse(res) {
				it!!.also { logService.trackEvent(EVENT_DECLINE_PACK_ITEM) }
			}
		}.onFailure {
			logService.trackError(
				EVENT_DECLINE_PACK_ITEM,
				it,
				mapOf("recordDeclineRequest" to req.toString())
			)
		}.getOrDefault(Result.generalError())

	suspend fun startPackTask(taskId: String): Result<PrepDetail?> = runCatching {
		logService.trackEvent(EVENT_START_PACK_ONLY, mapOf("taskId" to taskId))
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = packTaskApi.startPackTask(taskId = taskId, userId = dks)
		return Result.fromApiResponse(res) {
			it!!.also {
				logService.trackEvent(EVENT_START_PACK_ONLY_RES, mapOf("taskId" to taskId))
			}
		}
	}.onFailure {
		logService.trackError(EVENT_START_PACK_ONLY, it, mapOf("taskId" to taskId))
	}.getOrDefault(Result.generalError())

	suspend fun completePackAndGetHoldSlip(taskId: String): Result<StageTask?> = runCatching {
		logService.trackEvent(EVENT_COMPLETE_PACK, mapOf("taskId" to taskId))
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = packTaskApi.completePackAndGetHoldSlip(taskId = taskId, userId = dks)
		return Result.fromApiResponse(res) {
			it!!.also { task ->
				logService.trackEvent(EVENT_COMPLETE_PACK_RES, toStageTaskResMap(taskId, task))
			}
		}
	}.onFailure {
		logService.trackError(EVENT_COMPLETE_PACK, it, mapOf("taskId" to taskId))
	}.getOrDefault(Result.generalError())

	suspend fun packAndGetHoldSlip(taskId: String): Result<StageTask?> = runCatching {
		logService.trackEvent(EVENT_PACK, mapOf("taskId" to taskId))
		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = packTaskApi.packAndGetHoldSlip(taskId = taskId, userId = dks)
		return Result.fromApiResponse(res) {
			it!!.also { task ->
				logService.trackEvent(EVENT_PACK_RES, toStageTaskResMap(taskId, task))
			}
		}
	}.onFailure {
		logService.trackError(EVENT_PACK, it, mapOf("taskId" to taskId))
	}.getOrDefault(Result.generalError())

	private fun toPrepDetailsResMap(prepDetail: PrepDetail) = mapOf(
		"frNo" to prepDetail.fulfillmentRequestNumber,
		"fulfillmentType" to prepDetail.fulfillmentType,
		"subFulfillmentType" to prepDetail.subFulfillmentType,
		"skus" to prepDetail.items.joinToString { item -> item.sku },
		"assembleTaskId" to prepDetail.assembleTask?.taskId.toString(),
		"assembleStatus" to prepDetail.assembleTask?.status?.name.orEmpty(),
		"packTaskId" to prepDetail.packTask?.taskId.toString(),
		"packTaskStatus" to prepDetail.packTask?.status?.name.orEmpty()
	)

	private fun toStageTaskResMap(taskId: String, res: StageTask): Map<String, String> {
		val items = res.containers.flatMap { c -> c.packedItems }
		return mapOf(
			"taskId" to taskId,
			"frNo" to res.fulfillmentRequestNumber,
			"skus" to items.joinToString { item -> item.sku },
			"upcs" to items.mapNotNull { item -> item.scannedUpc }.joinToString(),
			"holdSlipPageCount" to res.holdSlipZPL.size.toString(),
			"containerCount" to res.containers.size.toString()
		)
	}
}
