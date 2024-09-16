package com.nextuple.nsf.ui.nav

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nextuple.nsf.ui.nav.NavUtil.ORDERS_PICKUP_DEEP_LINK
import com.nextuple.nsf.ui.nav.NavUtil.PICK_DEEP_LINK
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavUtilTest {

	@Test
	fun `getStartDestination will return LOGIN route when loggedIn is false`() {
		val expectedRoute = NavUtil.getStartDestination(false, Uri.EMPTY)
		assertEquals(Screen.LOGIN.route, expectedRoute)
	}

	@Test
	fun `getStartDestination will return Pick route when loggedIn is true and deepLinkUri is null`() {
		val expectedRoute = NavUtil.getStartDestination(true, Uri.EMPTY)
		assertEquals(Screen.PICK.route, expectedRoute)
	}

	@Test
	fun `getStartDestination will return Pick route when loggedIn is true and deepLinkUri is not found`() {
		val expectedRoute = NavUtil.getStartDestination(true, Uri.parse("nsf://nothing"))
		assertEquals(Screen.PICK.route, expectedRoute)
	}

	@Test
	fun `getStartDestination will return ORDERS route when loggedIn is true and deepLinkUri is not found`() {
		val expectedRoute =
			NavUtil.getStartDestination(true, Uri.parse("nsf://$ORDERS_PICKUP_DEEP_LINK"))
		assertEquals(Screen.ORDERS.route, expectedRoute)
	}

	@Test
	fun `getStartDestination will return PICK route when loggedIn is true and deepLinkUri is PICK_DEEP_LINK`() {
		val expectedRoute = NavUtil.getStartDestination(true, Uri.parse("nsf://$PICK_DEEP_LINK"))
		assertEquals(Screen.PICK.route, expectedRoute)
	}
}
