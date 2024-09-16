package com.nextuple.nsf.service

import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.ConfigApi
import com.nextuple.nsf.retrofit.api.StoreConfig
import com.nextuple.nsf.service.dto.Result
import javax.inject.Inject

class ConfigService @Inject constructor(
	private val logService: LogService,
	private val configApi: ConfigApi,
	private val userRepository: UserRepository
) {
	private var storeConfig: StoreConfig? = null

	suspend fun getStoreConfig(): Result<StoreConfig> {
		val store = userRepository.getStore() ?: return Result.generalError()
		val userId = userRepository.getUserId() ?: return Result.generalError()

		return if (storeConfig != null) {
			Result.Success(storeConfig!!)
		} else {
			val res = configApi.getStoreConfig(userId = userId, store = store.id)
			return runCatching {
				Result.fromApiResponse(res) {
					storeConfig = it
					it!!
				}
			}.onFailure {
				logService.trackError(
					attemptedAction = "getStoreConfig",
					t = it,
					additionalProps = mapOf(
						"store" to store.id,
						"userId" to userId
					)
				)
			}.getOrDefault(Result.generalError())
		}
	}

	fun clearStoreConfig() {
		storeConfig = null
	}
}
