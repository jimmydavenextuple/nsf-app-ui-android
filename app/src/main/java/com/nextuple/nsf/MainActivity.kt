package com.nextuple.nsf

import android.content.Context
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
import com.google.gson.Gson
import com.nextuple.nsf.service.DeviceService
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.LogService.Companion.EVENT_INACTIVITY_TIMEOUT
import com.nextuple.nsf.service.dto.User
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

		setContent {
			AppTheme {
				App(
					appVM = appVM,
					userVM = getUserState(),
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
					deviceService = deviceService
				)
			}
		}

		//firebase
		FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
			if (!task.isSuccessful) {
				Log.w(Constants.MessageNotificationKeys.TAG, "Fetching FCM registration token failed", task.exception)
				return@OnCompleteListener
			}

			// Get new FCM registration token
			val token = task.result

			// Log and toast
			Log.d("TAG", "FCM registration token: $token")
			Toast.makeText(baseContext,  token, Toast.LENGTH_SHORT).show()
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
		saveUserState()
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
	}

	companion object {
		private const val SESSION_TIMEOUT_IN_MILLIS: Long = 30 * 60 * 1000L
	}

	private fun saveUserState(){
		val userIsLoggedIn = userVM.user != null
		if(userIsLoggedIn){
			val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
			val editor = sharedPreferences.edit()
			val gson = Gson()
			editor.putBoolean("isUserLoggedIn", true)
			editor.putString("userInfo", gson.toJson(userVM.user).toString())
			editor.putString("errorMsg", userVM.errMsg)
			editor.putString("viewState", gson.toJson(userVM.viewState).toString())
			editor.apply()
		}
	}

	private fun getUserState(): UserViewModel {
		val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
		val isUserLoggedIn = sharedPreferences.getBoolean("isUserLoggedIn", false)
		if (isUserLoggedIn){
			val userInfo = sharedPreferences.getString("userInfo", "")
			val errorMsg = sharedPreferences.getString("errorMsg","")
			val gson = Gson()
			val user = gson.fromJson(userInfo, User::class.java)
			return UserViewModel(user = user, errMsg = errorMsg, userService = null, handler = null)
		}
		else{
			return userVM
		}
	}
}
