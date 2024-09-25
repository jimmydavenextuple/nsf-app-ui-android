package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.DemoApi
import com.nextuple.nsf.retrofit.api.InfoApi
import com.nextuple.nsf.retrofit.dto.response.DemoCreateFRResponse
import com.nextuple.nsf.retrofit.dto.response.GetDeclineCodesResponse
import com.nextuple.nsf.retrofit.dto.response.GetUserPickTasksResponse
import com.nextuple.nsf.retrofit.dto.response.GetUserPrepTasksResponse
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.util.FulfillmentType
import com.nextuple.nsf.util.SubFulfillmentType

class DemoService(
	private val demoApi: DemoApi,
	private val userRepository: UserRepository
) {

	suspend fun createFRBOPIS(option: Int): Result<List<DemoCreateFRResponse?>> {
		val store = userRepository.getStore() ?: return Result.generalError()
		val userId = userRepository.getUserId() ?: return Result.generalError()
		val res = demoApi.createFRBOPIS(option = option, store = store.id, userId = userId)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}

	suspend fun createFRSDD(option: Int): Result<List<DemoCreateFRResponse?>> {
		val store = userRepository.getStore() ?: return Result.generalError()
		val userId = userRepository.getUserId() ?: return Result.generalError()
		val res = demoApi.createFRSDD(option = option, store = store.id, userId = userId)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}
}
