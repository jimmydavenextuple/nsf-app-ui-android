package com.nextuple.nsf.service

import com.nextuple.nsf.retrofit.api.PrepApi
import com.nextuple.nsf.retrofit.dto.PackTask
import com.nextuple.nsf.retrofit.dto.RecordHoldingLocationRequest
import com.nextuple.nsf.retrofit.dto.StageTask
import com.nextuple.nsf.service.dto.Result

class PrepService(private val prepApi: PrepApi) {

	suspend fun startPack(taskId: String, dks: String): Result<PackTask?> {
		val res = prepApi.startPack(taskId = taskId, userId = dks)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun packAndGetHoldSlip(taskId: String, dks: String): Result<StageTask?> {
		val res = prepApi.packAndGetHoldSlip(taskId = taskId, userId = dks)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun recordHoldingLocation(
		recordHoldingLocationRequest: RecordHoldingLocationRequest,
		dks: String
	): Result<StageTask?> {
		val res = prepApi.recordHoldingLocation(recordHoldingLocationRequest = recordHoldingLocationRequest, userId = dks)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}
}
