package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.InfoApi
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.retrofit.dto.response.GetUserPickTasksResponse
import com.nextuple.nsf.retrofit.dto.response.GetUserPrepTasksResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.util.FulfillmentType
import com.nextuple.nsf.util.SubFulfillmentType

class InfoService(
	private val infoApi: InfoApi,
	private val userRepository: UserRepository
) {

	suspend fun getStoreOverview(): Result<StoreOverviewResponse?> {
		val store = userRepository.getStore() ?: return Result.generalError()
		val userId = userRepository.getUserId() ?: return Result.generalError()
		val res = infoApi.getStoreOverview(store = store.id, userId = userId)

		return runCatching {
			Result.fromApiResponse(res) {
				it!!
				StoreOverviewResponse(
					pickOverview = it.pickOverview,
					prepOverview = it.prepOverview
				)
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun getUserPickTasks(): Result<GetUserPickTasksResponse?> {
		val store = userRepository.getStore() ?: return Result.generalError()
		val userId = userRepository.getUserId() ?: return Result.generalError()
		val res = infoApi.getUserPickTasks(store = store.id, userId = userId)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun getUserSfsPickTasks(): Result<GetUserPickTasksResponse?> {
		val store = userRepository.getStore() ?: return Result.generalError()
		val userId = userRepository.getUserId() ?: return Result.generalError()
		val res = infoApi.getUserPickTasks(
			store = store.id, 
			userId = userId,
			fulfillmentType = FulfillmentType.SFS.name
		)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun getUserPrepTasks(): Result<GetUserPrepTasksResponse?> {
		val store = userRepository.getStore() ?: return Result.generalError()
		val userId = userRepository.getUserId() ?: return Result.generalError()
		val res = infoApi.getUserPrepTasks(store = store.id, userId = userId)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun getDeclineCodes(): Result<GetDeclineCodesResponse> {
		val store = userRepository.getStore() ?: return Result.generalError()
		val userId = userRepository.getUserId() ?: return Result.generalError()

		val res = infoApi.getDeclineCodes(
			brand = store.brand.chainName,
			store = store.id,
			fulfillmentType = FulfillmentType.BOPIS.name,
			subFulfillmentType = SubFulfillmentType.BOPIS.name,
			userId = userId
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
