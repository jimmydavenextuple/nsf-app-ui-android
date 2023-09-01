package com.nextuple.nsf.service

import com.nextuple.nsf.service.dto.Device
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeviceServiceTest {

	@InjectMockKs
	private lateinit var deviceService: DeviceService

	@MockK(relaxed = true)
	private lateinit var logService: LogService

	@MockK
	private lateinit var getDeviceModel: () -> String

	@MockK
	private lateinit var getDeviceId: () -> String

	@MockK
	private lateinit var getMacAddress: () -> String

	@Before
	fun setUp() {
		MockKAnnotations.init(this)
	}

	@Test
	fun `getDevice should return the expected device info`() {
		val device = Device(
			model = "testModel",
			deviceId = "testDeviceId",
			macAddress = "testMacAddress"
		)
		every { getDeviceModel() } returns device.model
		every { getDeviceId() } returns device.deviceId
		every { getMacAddress() } returns device.macAddress

		val res = deviceService.getDevice()

		assertEquals(device, res)
	}

	@Test
	fun `getDevice should gracefully handle failure to fetch device model`() {
		val device = Device(
			model = "",
			deviceId = "testDeviceId",
			macAddress = "testMacAddress"
		)
		every { getDeviceModel() } throws NullPointerException()
		every { getDeviceId() } returns device.deviceId
		every { getMacAddress() } returns device.macAddress

		val res = deviceService.getDevice()

		assertEquals(device, res)
	}

	@Test
	fun `getDevice should gracefully handle failure to fetch device id`() {
		val device = Device(
			model = "testModel",
			deviceId = "",
			macAddress = "testMacAddress"
		)
		every { getDeviceModel() } returns device.model
		every { getDeviceId() } throws NullPointerException()
		every { getMacAddress() } returns device.macAddress

		val res = deviceService.getDevice()

		assertEquals(device, res)
	}

	@Test
	fun `getDevice should gracefully handle failure to fetch device MAC address`() {
		val device = Device(
			model = "testModel",
			deviceId = "testDeviceId",
			macAddress = ""
		)
		every { getDeviceModel() } returns device.model
		every { getDeviceId() } returns device.deviceId
		every { getMacAddress() } throws NullPointerException()

		val res = deviceService.getDevice()

		assertEquals(device, res)
	}
}
