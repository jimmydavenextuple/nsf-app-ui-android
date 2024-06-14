package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.UserApi
import com.nextuple.nsf.retrofit.dto.LoginRequest
import com.nextuple.nsf.service.LogService.Companion.EVENT_LOGIN
import com.nextuple.nsf.service.LogService.Companion.EVENT_LOGIN_RES
import com.nextuple.nsf.service.LogService.Companion.EVENT_LOGOUT
import com.nextuple.nsf.service.dto.Brand
import com.nextuple.nsf.service.dto.Result
import com.nextuple.nsf.service.dto.Result.Companion.fromApiResponse
import com.nextuple.nsf.service.dto.Result.Companion.generalError
import com.nextuple.nsf.service.dto.Store
import com.nextuple.nsf.service.dto.User

class UserService(
	private val userApi: UserApi,
	private val logService: LogService,
	private val userRepository: UserRepository
) {
	suspend fun login(nodeNo: String, userId: String): Result<User> {
		// Include common props for this one since they don't officially populate until result.
		logService.trackEvent(
			EVENT_LOGIN,
			mapOf(
				"userId" to userId,
				"nodeNo" to nodeNo,
				"brand" to Brand.NT_BRAND_A.toString()
			)
		)

		val res = userApi.login(LoginRequest(nodeNo = nodeNo, userId = userId))
		return runCatching {
			fromApiResponse(res) {
				it!!
				User(
					firstName = it.firstName,
					lastName = it.lastName,
					userId = it.userId,
					store = Store(id = it.nodeNo, brand = Brand.NT_BRAND_A)
				).also { user ->
					logService.setUser(user)
					logService.trackEvent(EVENT_LOGIN_RES)
				}
			}
		}.onSuccess {
			if (it is Result.Success) {
				userRepository.updateUser(
					firstName = it.data.firstName,
					lastName = it.data.lastName,
					userId = it.data.userId,
					nodeNo = it.data.store.id,
					brand = it.data.store.brand.toString()
				)
			}
		}.onFailure {
			// Include common props for this one since they don't officially populate until result.
			logService.trackError(
				EVENT_LOGIN,
				it,
				mapOf(
					"userId" to userId,
					"nodeNo" to nodeNo,
					"brand" to Brand.NT_BRAND_A.toString()
				)
			)
		}.getOrDefault(generalError())
	}

	suspend fun logout() {
		logService.trackEvent(EVENT_LOGOUT)
		logService.setUser(null)
		userRepository.clearUser()
	}
}
