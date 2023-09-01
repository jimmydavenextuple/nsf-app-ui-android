package com.nextuple.nsf.service

import com.nextuple.nsf.retrofit.api.AppApi
import com.nextuple.nsf.retrofit.dto.response.StoreOverviewResponse
import com.nextuple.nsf.service.dto.Result

class AppService(
	private val appApi: AppApi,
	private val deviceService: DeviceService
) {

	suspend fun getStoreOverview(dks: String): Result<StoreOverviewResponse?> {
		val store = deviceService.getStore()
		val res = appApi.getStoreOverview(store = store.id, userId = dks)
		return runCatching {
			Result.fromApiResponse(res) {
				it!!
				StoreOverviewResponse(
					pickOverview = it.pickOverview,
					userOverview = it.userOverview,
					prepOverview = it.prepOverview
				)
			}
		}.onFailure {
			// TODO: Log as non-fatal exception.
		}.getOrDefault(Result.generalError())
	}
}
