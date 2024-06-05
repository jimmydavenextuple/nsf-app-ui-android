package com.nextuple.nsf

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nextuple.nsf.messaging.MyFirebaseMessagingService
import com.nextuple.nsf.service.DeviceService
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.LogService.Companion.EVENT_INACTIVITY_TIMEOUT
import com.nextuple.nsf.ui.App
import com.nextuple.nsf.ui.state.ConfigViewModel
import com.nextuple.nsf.ui.state.InfoViewModel
import com.nextuple.nsf.ui.state.OrderViewModel
import com.nextuple.nsf.ui.state.SettingsViewModel
import com.nextuple.nsf.ui.state.UserViewModel
import com.nextuple.nsf.ui.theme.AppTheme
import com.nextuple.nsf.ui.util.Haptics
import com.nextuple.nsf.ui.util.OnDataScanned
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private val userVM: UserViewModel by viewModels()
	private val orderVM: OrderViewModel by viewModels()
	private val settingsVM: SettingsViewModel by viewModels()
	private val infoVM: InfoViewModel by viewModels()
	private val configVM: ConfigViewModel by viewModels()

	@Inject
	lateinit var logService: LogService

	@Inject
	lateinit var deviceService: DeviceService

	@Inject
	lateinit var dataWedge: DataWedgeBroadcastReceiver

	@Inject
	lateinit var haptics: Haptics

	private val handler = Handler(Looper.getMainLooper())

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		logService.start()
		logService.setDevice(deviceService.getDevice())

		userVM.setOnLogoutCallbacks(
			infoVM::onLogout
		)
		settingsVM.setIpPrefix()

		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				userVM.handleTimeoutLogout(SESSION_TIMEOUT_IN_MILLIS)
			}
		}
		setContent {
			AppTheme {
				App(
					infoVM = infoVM,
					userVM = userVM,
					orderVM = orderVM,
					settingsVM = settingsVM,
					configVM = configVM,
					scanManager = object : ScanManager {
						override fun set(onDataScanned: OnDataScanned) {
							dataWedge.setOnDataScanned(
								logService = logService,
								onDataScanned = onDataScanned
							)
						}
					},
					haptics = haptics,
					intentData = intent.data,
					onLoggedIn = ::startSessionListener
				)
			}
		}

		// firebase
		FirebaseMessaging.getInstance().token.addOnCompleteListener(
			OnCompleteListener { task ->
				if (!task.isSuccessful) {
					logService.trackError(
						"FailedFetchingFireBaseToken",
						Throwable(task.exception),
						mapOf("Details" to task.result)
					)
					return@OnCompleteListener
				}

				// Get new FCM registration token
				val token = task.result

				// Log and toast
				logService.trackEvent(
					"FetchedFireBaseToken",
					mapOf("FirebaseToken" to token)
				)
// 				if(BuildConfig.DEBUG) {
// 					Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
// 				}
				MyFirebaseMessagingService.sendRegistrationToServer(token)
			}
		)
	}

	override fun onStart() {
		super.onStart()
		dataWedge.registerSelf(this)
	}

	override fun onStop() {
		dataWedge.unregisterSelf(this)
		super.onStop()
	}

	override fun onPause() {
		super.onPause()
		userVM.updateUserActivity()
	}

	override fun onUserInteraction() {
		super.onUserInteraction()
		startSessionListener()
	}

	private fun startSessionListener() {
		if (userVM.isLoggedIn()) {
			// check the user's activeness after a specified time in milliseconds
			handler.removeCallbacks(runnable)
			handler.postDelayed(runnable, SESSION_TIMEOUT_IN_MILLIS)
		}
	}

	private val runnable = kotlinx.coroutines.Runnable {
		logService.trackEvent(EVENT_INACTIVITY_TIMEOUT)
		userVM.logout()
	}

	companion object {
		private const val SESSION_TIMEOUT_IN_MILLIS: Long = 30 * 60 * 1000L
	}
}
