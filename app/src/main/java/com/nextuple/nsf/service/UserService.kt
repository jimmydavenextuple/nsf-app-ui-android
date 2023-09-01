package com.nextuple.nsf.service

import com.nextuple.nsf.retrofit.api.UserApi
import com.nextuple.nsf.retrofit.dto.LoginRequest
import com.nextuple.nsf.service.LogService.Companion.EVENT_LOGIN
import com.nextuple.nsf.service.LogService.Companion.EVENT_LOGOUT
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Result.Companion.fromApiResponse
import com.nextuple.nsf.service.dto.Result.Companion.generalError
import com.nextuple.nsf.service.dto.User

class UserService(
	private val userApi: UserApi,
	private val logService: LogService,
	private val deviceService: DeviceService
) {
	suspend fun login(dks: String): Result<User> {
		val store = deviceService.getStore()
		println("Logging in for brand ${store.brand} store ${store.id}")

		val res = userApi.login(LoginRequest(dks = dks, store = store.id))
		return runCatching {
			fromApiResponse(res) {
				it!!
				User(
					firstName = it.firstName,
					lastName = it.lastName,
					dks = dks,
					store = store
				).also { user ->
					logService.setUser(user)
					logService.trackEvent(EVENT_LOGIN)
				}
			}
		}.onFailure {
			logService.trackError(
				EVENT_LOGIN,
				it,
				mapOf(
					"dks" to dks,
					"storeId" to store.id,
					"storeBrand" to store.brand.chainName
				)
			)
		}.getOrDefault(generalError())
	}

	fun logout() {
		logService.trackEvent(EVENT_LOGOUT)
		logService.setUser(null)
	}
}
