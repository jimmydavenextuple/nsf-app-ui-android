package com.nextuple.nsf.service

import android.app.Application
import android.os.Build
import android.util.Log
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
// 		if (!AppCenter.isConfigured()) {
// 			AppCenter.start(
// 				app,
// 				BuildConfig.APP_CENTER_KEY,
// 				Analytics::class.java,
// 				Crashes::class.java
// 			)
// 		}
//
// 		Clarity.initialize(app, ClarityConfig(BuildConfig.CLARITY_PROJECT_ID))

		Log.i("LOG", "Initialize logging")
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
		Log.d(eventName, "EventProps: $additionalProps")
		// Analytics.trackEvent(eventName, commonProps().plus(additionalProps))
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
		// Integration with
// 		Crashes.trackError(
// 			t,
// 			mapOf("attemptedAction" to attemptedAction)
// 				.plus(commonProps())
// 				.plus(additionalProps),
// 			emptyList()
// 		)
		Log.e(attemptedAction, "Action: $attemptedAction, Error: $additionalProps", t)
	}

	private fun commonProps(): Map<String, String> = buildConfigToProps().plus(userToProps())

	private fun userToProps(): Map<String, String> = user?.let {
		mapOf(
			"userDks" to it.userId,
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
		const val EVENT_LOGIN_RES = "LoginRes"
		const val EVENT_LOGOUT = "Logout"
		const val EVENT_INACTIVITY_TIMEOUT = "InactivityTimeout"

		// Pick
		const val EVENT_PICK_START = "PickStart"
		const val EVENT_PICK_START_RES = "PickStartRes"
		const val EVENT_PICK_ITEM = "PickItem"
		const val EVENT_PICK_ITEM_RES = "PickItemRes"
		const val EVENT_PICK_DECLINE = "PickDecline"
		const val EVENT_PICK_DECLINE_RES = "PickDeclineRes"

		// Prep
		const val EVENT_PACK = "Pack"
		const val EVENT_PACK_RES = "PackRes"
		const val EVENT_START_PACK_ONLY = "StartPackOnly"
		const val EVENT_START_PACK_ONLY_RES = "StartPackOnlyRes"
		const val EVENT_COMPLETE_PACK = "CompletePack"
		const val EVENT_COMPLETE_PACK_RES = "CompletePackRes"
		const val EVENT_STAGE = "Stage"
		const val EVENT_STAGE_RES = "StageRes"
		const val EVENT_GET_HOLD_SLIP = "GetHoldSlip"
		const val EVENT_GET_HOLD_SLIP_RES = "GetHoldSlipRes"
		const val EVENT_GET_PREP_DETAILS = "GetPrepDetails"
		const val EVENT_DECLINE_PACK_ITEM = "DeclinePackItem"
		const val EVENT_PACK_BY_GEAR = "PackByGear"
		const val EVENT_PACK_BY_GEAR_RES = "PackByGearRes"
		const val EVENT_PACK_BY_ORDER = "PackByOrder"
		const val EVENT_PACK_BY_ORDER_RES = "PackByOrderRes"
		const val EVENT_GET_PREP_DETAILS_RES = "GetPrepDetailsRes"

		// Order Details
		const val EVENT_AGED_ORDER_CANCEL = "AgedOrderCancel"
		const val EVENT_ORDER_CANCEL = "OrderCancel"
	}
}
