package com.nextuple.nsf.service

import android.app.Application
import android.content.Context
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.clarity.Clarity
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.service.dto.Brand.PL
import com.nextuple.nsf.service.dto.Device
import com.nextuple.nsf.service.dto.Store
import com.nextuple.nsf.service.dto.User
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
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

		mockkStatic(AppCenter::class)
		every { AppCenter.getInstance() } returns mockk(relaxed = true)

		mockkStatic(Clarity::class)
		every { Clarity.initialize(any<Context>(), any()) } returns mockk(relaxed = true)
	}

	@After
	fun tearDown() {
		unmockkStatic(AppCenter::class, Analytics::class, Crashes::class, Clarity::class)
	}

	@Test
	fun `start should call AppCenter start if not already configured`() {
		every { AppCenter.isConfigured() } returns false
		justRun { AppCenter.start(any(), any<String>(), any()) }

		logService.start()

		verify(exactly = 1) {
			AppCenter.start(
				any(),
				BuildConfig.APP_CENTER_KEY,
				Analytics::class.java,
				Crashes::class.java
			)
		}
	}

	@Test
	fun `start should not call AppCenter start if already configured`() {
		every { AppCenter.isConfigured() } returns true

		logService.start()

		verify(exactly = 0) {
			AppCenter.start(any(), any<String>(), any())
		}
	}

	@Test
	fun `trackEvent should include common and additional props`() {
		val eventName = "testEvent"
		val user = User(
			firstName = "testFirst",
			lastName = "testLast",
			dks = "testDks",
			store = Store(id = "1", brand = PL)
		)
		val device = Device(
			model = "testModel",
			deviceId = "testDeviceId",
			macAddress = "testMacAddress"
		)
		val additionalProp = "testAdditionalPropKey" to "testAdditionalPropValue"

		mockkStatic(Analytics::class)
		justRun { Analytics.trackEvent(any(), any<Map<String, String>>()) }

		logService.apply {
			setUser(user)
			setDevice(device)
			trackEvent(eventName, mapOf(additionalProp))
		}

		verify {
			Analytics.trackEvent(
				eventName,
				match<Map<String, String>> {
					it.containsKey(additionalProp.first) &&
						it[additionalProp.first] == additionalProp.second &&
						it.containsValue(user.firstName) &&
						it.containsValue(user.lastName) &&
						it.containsValue(user.dks) &&
						it.containsValue(user.store.id) &&
						it.containsValue(user.store.brand.chainName) &&
						it.containsValue(device.model) &&
						it.containsValue(device.deviceId) &&
						it.containsValue(device.macAddress)
				}
			)
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
			store = Store(id = "1", brand = PL)
		)
		val device = Device(
			model = "testModel",
			deviceId = "testDeviceId",
			macAddress = "testMacAddress"
		)
		val additionalProp = "testAdditionalPropKey" to "testAdditionalPropValue"

		mockkStatic(Crashes::class)
		justRun { Crashes.trackError(any(), any(), any()) }

		logService.apply {
			setUser(user)
			setDevice(device)
			trackError(action, t, mapOf(additionalProp))
		}

		verify {
			Crashes.trackError(
				t,
				match<Map<String, String>> {
					it.containsKey(additionalProp.first) &&
						it[additionalProp.first] == additionalProp.second &&
						it.containsValue(user.firstName) &&
						it.containsValue(user.lastName) &&
						it.containsValue(user.dks) &&
						it.containsValue(user.store.id) &&
						it.containsValue(user.store.brand.chainName) &&
						it.containsValue(device.model) &&
						it.containsValue(device.deviceId) &&
						it.containsValue(device.macAddress)
				},
				any()
			)
		}
	}
}
