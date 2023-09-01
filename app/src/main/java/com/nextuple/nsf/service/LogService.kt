package com.nextuple.nsf.service

import android.app.Application
import android.os.Build
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.service.dto.Device
import com.nextuple.nsf.service.dto.User

/**
 * Other services should not be injected into this directly to avoid circular dependencies.
 * Instead, inject DTOs via setters as needed to add commonly logged properties.
 */
class LogService(private val app: Application) {

	private var user: User? = null
	private var device: Device? = null

	fun start() {
		if (!AppCenter.isConfigured()) {
			AppCenter.start(
				app,
				BuildConfig.APP_CENTER_KEY,
				Analytics::class.java,
				Crashes::class.java
			)
		}

		Clarity.initialize(app, ClarityConfig(BuildConfig.CLARITY_PROJECT_ID))
	}

	fun setUser(user: User?) {
		this.user = user
	}

	fun setDevice(device: Device) {
		this.device = device
	}

	/**
	 * @param eventName the event name
	 * @param additionalProps additional properties not already covered under [User] and [Device]
	 */
	fun trackEvent(eventName: String, additionalProps: Map<String, String> = emptyMap()) {
		Analytics.trackEvent(eventName, commonProps().plus(additionalProps))
	}

	/**
	 * @param attemptedAction a value indicating the action attempted
	 * @param t the throwable
	 * @param additionalProps additional properties not already covered under [User] and [Device]
	 */
	fun trackError(
		attemptedAction: String,
		t: Throwable,
		additionalProps: Map<String, String> = emptyMap()
	) {
		Crashes.trackError(
			t,
			mapOf("attemptedAction" to attemptedAction)
				.plus(commonProps())
				.plus(additionalProps),
			emptyList()
		)
	}

	private fun commonProps(): Map<String, String> = buildConfigToProps().plus(userToProps())

	private fun userToProps(): Map<String, String> = user?.let {
		mapOf(
			"userDks" to it.dks,
			"userFirstName" to it.firstName,
			"userLastName" to it.lastName,
			"storeId" to it.store.id,
			"storeBrand" to it.store.brand.chainName
		)
	} ?: emptyMap()

	private fun buildConfigToProps(): Map<String, String> = mapOf(
		"appVersion" to BuildConfig.VERSION_NAME,
		"deviceModel" to (device?.model ?: "fallbackValue_${Build.MODEL}"),
		"deviceId" to (device?.deviceId ?: "fallbackValue_${Build.ID}"),
		"deviceMacAddress" to device?.macAddress.orEmpty(),
		"buildType" to BuildConfig.BUILD_TYPE,
		"buildIsDebug" to BuildConfig.DEBUG.toString()
	)

	companion object {
		// User
		const val EVENT_LOGIN = "Login"
		const val EVENT_LOGOUT = "Logout"
		const val EVENT_INACTIVITY_TIMEOUT = "InactivityTimeout"
	}
}
