package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.StageTaskApi
import com.nextuple.nsf.retrofit.dto.RecordHoldingLocationRequest
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.service.LogService.Companion.EVENT_GET_HOLD_SLIP
import com.nextuple.nsf.service.LogService.Companion.EVENT_GET_HOLD_SLIP_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_STAGE
import com.nextuple.nsf.service.LogService.Companion.EVENT_STAGE_RES
import com.nextuple.nsf.service.dto.Result

class StageTaskService(
	private val stageTaskApi: StageTaskApi,
	private val userRepository: UserRepository,
	private val logService: LogService
) {

	suspend fun getHoldSlip(
		fulfillmentRequestNumber: String
	): Result<StageTask?> = runCatching {
		logService.trackEvent(EVENT_GET_HOLD_SLIP, mapOf("frNo" to fulfillmentRequestNumber))

		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = stageTaskApi.getHoldSlip(
			fulfillmentRequestNumber = fulfillmentRequestNumber,
			userId = dks
		)
		Result.fromApiResponse(res) {
			it!!.also { task ->
				val items = task.containers.flatMap { c -> c.packedItems }

				logService.trackEvent(
					EVENT_GET_HOLD_SLIP_RES,
					mapOf(
						"frNo" to fulfillmentRequestNumber,
						"skus" to items.joinToString { item -> item.sku },
						"upcs" to items.mapNotNull { item -> item.scannedUpc }.joinToString(),
						"holdSlipPageCount" to task.holdSlipZPL.size.toString(),
						"containerCount" to task.containers.size.toString()
					)
				)
			}
		}
	}.onFailure {
		logService.trackError(
			EVENT_GET_HOLD_SLIP,
			it,
			mapOf("frNo" to fulfillmentRequestNumber)
		)
	}.getOrDefault(Result.generalError())

	suspend fun recordHoldingLocation(
		req: RecordHoldingLocationRequest
	): Result<StageTask?> = runCatching {
		logService.trackEvent(
			EVENT_STAGE,
			mapOf(
				"containerId" to req.containerId.toString(),
				"holdingLocation" to req.holdingLocation,
				"containerLocationPair" to "${req.containerId} ${req.holdingLocation}"
			)
		)

		val dks = userRepository.getDks() ?: return Result.generalError()
		val res = stageTaskApi.recordHoldingLocation(
			recordHoldingLocationRequest = req,
			userId = dks
		)
		Result.fromApiResponse(res) {
			it!!.also { task ->
				val items = task.containers.flatMap { c -> c.packedItems }

				logService.trackEvent(
					EVENT_STAGE_RES,
					mapOf(
						"taskId" to task.id.toString(),
						"frNo" to task.fulfillmentRequestNumber,
						"skus" to items.joinToString { item -> item.sku },
						"upcs" to items.mapNotNull { item -> item.scannedUpc }.joinToString(),
						"holdSlipPageCount" to task.holdSlipZPL.size.toString(),
						"containerCount" to task.containers.size.toString()
					)
				)
			}
		}
	}.onFailure {
		logService.trackError(
			EVENT_STAGE,
			it,
			mapOf(
				"containerId" to req.containerId.toString(),
				"holdingLocation" to req.holdingLocation,
				"containerLocationPair" to "${req.containerId} ${req.holdingLocation}"
			)
		)
	}.getOrDefault(Result.generalError())
}
