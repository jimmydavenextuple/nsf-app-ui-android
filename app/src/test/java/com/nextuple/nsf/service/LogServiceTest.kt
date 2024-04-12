package com.nextuple.nsf.service

import android.app.Application
import com.nextuple.nsf.service.dto.Brand.NT_BRAND_A
import com.nextuple.nsf.service.dto.Device
import com.nextuple.nsf.service.dto.Store
import com.nextuple.nsf.service.dto.User
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Before
import org.junit.Test

class LogServiceTest {

	@InjectMockKs
	private lateinit var logService: LogService

	@MockK(relaxed = true)
	private lateinit var app: Application

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@After
	fun tearDown() {
	}

	@Test
	fun `start should call AppCenter start if not already configured`() {
		logService.start()
	}

	@Test
	fun `start should not call AppCenter start if already configured`() {
		logService.start()
	}

	@Test
	fun `trackEvent should include common and additional props`() {
		val eventName = "testEvent"
		val user = User(
			firstName = "testFirst",
			lastName = "testLast",
			dks = "testDks",
			store = Store(id = "1", brand = NT_BRAND_A)
		)
		val device = Device(
			model = "testModel",
			deviceId = "testDeviceId",
			macAddress = "testMacAddress"
		)
		val additionalProp = "testAdditionalPropKey" to "testAdditionalPropValue"

		logService.apply {
			setUser(user)
			setDevice(device)
			trackEvent(eventName, mapOf(additionalProp))
		}
	}

	@Test
	fun `trackError should include common and additional props`() {
		val action = "testAction"
		val t = Throwable("testThrow")
		val user = User(
			firstName = "testFirst",
			lastName = "testLast",
			dks = "testDks",
			store = Store(id = "1", brand = NT_BRAND_A)
		)
		val device = Device(
			model = "testModel",
			deviceId = "testDeviceId",
			macAddress = "testMacAddress"
		)
		val additionalProp = "testAdditionalPropKey" to "testAdditionalPropValue"

		logService.apply {
			setUser(user)
			setDevice(device)
			trackError(action, t, mapOf(additionalProp))
		}
	}
}
