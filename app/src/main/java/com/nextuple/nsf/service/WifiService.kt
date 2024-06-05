package com.nextuple.nsf.service

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WifiService @Inject constructor(
	@ApplicationContext private val context: Context
) {
	fun getIpAddressFromDevice(): String {
		val wifiMgr =
			context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

		// Min SDK at time of this change is 30, so we still need this.
		@Suppress("DEPRECATION")
		return Formatter.formatIpAddress(wifiMgr.connectionInfo.ipAddress)
	}
}
