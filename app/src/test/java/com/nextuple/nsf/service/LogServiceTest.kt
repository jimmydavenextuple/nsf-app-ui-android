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
	fun `trackEvent should include common and additional props`() {
		val user = User(
			firstName = "testFirst",
			lastName = "testLast",
			userId = "testUserId",
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
		}
	}

	@Test
	fun `trackError should include common and additional props`() {
		val user = User(
			firstName = "testFirst",
			lastName = "testLast",
			userId = "testUserId",
			store = Store(id = "1", brand = NT_BRAND_A)
		)
		val device = Device(
			model = "testModel",
			deviceId = "testDeviceId",
			macAddress = "testMacAddress"
		)

		logService.apply {
			setUser(user)
			setDevice(device)
		}
	}
}
