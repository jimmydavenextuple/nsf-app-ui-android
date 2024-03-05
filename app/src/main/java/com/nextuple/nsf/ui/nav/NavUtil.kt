package com.nextuple.nsf.ui.nav

import android.net.Uri

object NavUtil {

	const val ORDERS_PICKUP_DEEP_LINK = "orders_pickup"
	fun getStartDestination(isLoggedIn: Boolean, deepLinkUri: Uri?) =
		when {
			isLoggedIn && deepLinkUri == null -> Screen.PICK.route
			isLoggedIn && deepLinkUri != null -> getRouteForDeepLink(deepLinkUri)
			else -> Screen.LOGIN.route
		}

	private fun getRouteForDeepLink(deepLinkUri: Uri) =
		when {
			deepLinkUri.toString().contains(ORDERS_PICKUP_DEEP_LINK) -> Screen.ORDERS.route
			else -> Screen.PICK.route
		}
}
