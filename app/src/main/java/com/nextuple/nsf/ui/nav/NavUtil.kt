package com.nextuple.nsf.ui.nav

import android.net.Uri

object NavUtil {

	const val ORDERS_PICKUP_DEEP_LINK = "nsf://orders_pickup"
	const val PICK_DEEP_LINK = "nsf://pick"
	fun getStartDestination(isLoggedIn: Boolean, deepLinkUri: Uri?) =
		when {
			isLoggedIn && deepLinkUri == null -> Screen.PICK.route
			isLoggedIn && deepLinkUri != null -> getRouteForDeepLink(deepLinkUri)
			else -> Screen.LOGIN.route
		}

	private fun getRouteForDeepLink(deepLinkUri: Uri) =
		when {
			deepLinkUri.toString().contains(ORDERS_PICKUP_DEEP_LINK) -> Screen.ORDERS.route
			deepLinkUri.toString().contains(PICK_DEEP_LINK) -> Screen.PICK.route
			else -> Screen.PICK.route
		}
}
