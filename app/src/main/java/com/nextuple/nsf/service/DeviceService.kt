package com.nextuple.nsf.service

import com.nextuple.nsf.service.dto.Brand
import com.nextuple.nsf.service.dto.Device
import com.nextuple.nsf.service.dto.Store

class DeviceService(
	private val logService: LogService,
	private val getDeviceModel: () -> String,
	private val getDeviceId: () -> String,
	private val getMacAddress: () -> String
) {
	fun getStore(): Store {
		val store = 1234
		val brand = Brand.toBrand("DSG")
		return Store(
			id = store.toString(),
			brand = brand
		)
	}

	fun getDevice(): Device {
		val model = runCatching {
			getDeviceModel()
		}.onFailure {
			logService.trackError("getDeviceModel", it)
		}.getOrDefault("")

		val deviceId = runCatching {
			getDeviceId()
		}.onFailure {
			logService.trackError("getDeviceId", it)
		}.getOrDefault("")

		val macAddress = runCatching {
			getMacAddress()
		}.onFailure {
			logService.trackError("getMacAddress", it)
		}.getOrDefault("")

		return Device(
			model = model,
			deviceId = deviceId,
			macAddress = macAddress
		)
	}
}
