package com.nextuple.nsf

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.nextuple.nsf.service.DeviceService
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.LogService.Companion.EVENT_INACTIVITY_TIMEOUT
import com.nextuple.nsf.ui.App
import com.nextuple.nsf.ui.state.AppViewModel
import com.nextuple.nsf.ui.state.OrderViewModel
import com.nextuple.nsf.ui.state.PickViewModel
import com.nextuple.nsf.ui.state.PrepViewModel
import com.nextuple.nsf.ui.state.SettingsViewModel
import com.nextuple.nsf.ui.state.UserViewModel
import com.nextuple.nsf.ui.theme.AppTheme
import com.nextuple.nsf.ui.util.Haptics
import com.nextuple.nsf.ui.util.OnDataScanned
import com.nextuple.nsf.ui.util.ScanManager
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver
import com.nextuple.nsf.util.UserStateUtils.clearUserState
import com.nextuple.nsf.util.UserStateUtils.getUserState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private val userVM: UserViewModel by viewModels()
	private val pickVM: PickViewModel by viewModels()
	private val prepVM: PrepViewModel by viewModels()
	private val orderVM: OrderViewModel by viewModels()
	private val settingsVM: SettingsViewModel by viewModels()
	private val appVM: AppViewModel by viewModels()

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

		var loadPickScreenOnNotificationTap = intent.getBooleanExtra("loadPickScreenOnNotificationTap", false)

		if (intent.hasExtra("storeNumber")) {    // This code sets a boolean flag to open the "Pick Screen" when the notification is tapped, when the app is in the background or not running.
			loadPickScreenOnNotificationTap = true
		}

		val userState = getUserState(userVM, this);

		setContent {
			AppTheme {
				App(
					appVM = appVM,
					userVM = userState,
					pickVM = pickVM,
					prepVM = prepVM,
					orderVM = orderVM,
					settingsVM = settingsVM,
					scanManager = object : ScanManager {
						override fun set(onDataScanned: OnDataScanned) {
							dataWedge.setOnDataScanned(onDataScanned)
						}
					},
					haptics = haptics,
					onLoggedIn = ::startSessionListener,
					loadPickScreenOnNotificationTap = loadPickScreenOnNotificationTap,
					deviceService = deviceService,
					context = this
				)
			}
		}

		//firebase
		FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
			if (!task.isSuccessful) {
				Log.w(
					Constants.MessageNotificationKeys.TAG,
					"Fetching FCM registration token failed",
					task.exception
				)
				return@OnCompleteListener
			}

			// Get new FCM registration token
			val token = task.result

			// Log and toast
			Log.d("TAG", "FCM registration token: $token")
			Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
		})
	}

	override fun onStart() {
		super.onStart()
		dataWedge.registerSelf(this)
	}

	override fun onStop() {
		dataWedge.unregisterSelf(this)
		super.onStop()
	}

	override fun onUserInteraction() {
		super.onUserInteraction()
		startSessionListener()
	}

	private fun startSessionListener() {
		if (userVM.viewState == UserViewModel.ViewState.LoggedIn) {
			// check the user's activeness after a specified time in milliseconds
			handler.removeCallbacks(runnable)
			handler.postDelayed(runnable, SESSION_TIMEOUT_IN_MILLIS)
		}
	}

	private val runnable = kotlinx.coroutines.Runnable {
		logService.trackEvent(EVENT_INACTIVITY_TIMEOUT)
		userVM.logout()
		pickVM.onLogout()
		clearUserState(this)
	}

	companion object {
		private const val SESSION_TIMEOUT_IN_MILLIS: Long = 30 * 60 * 1000L
	}
}

