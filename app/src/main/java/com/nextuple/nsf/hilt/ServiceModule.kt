package com.nextuple.nsf.hilt

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.provider.Settings
import com.nextuple.nsf.retrofit.api.AppApi
import com.nextuple.nsf.retrofit.api.OrderApi
import com.nextuple.nsf.retrofit.api.PickApi
import com.nextuple.nsf.retrofit.api.PrepApi
import com.nextuple.nsf.retrofit.api.UserApi
import com.nextuple.nsf.service.AppService
import com.nextuple.nsf.service.DeviceService
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.OrderService
import com.nextuple.nsf.service.PickService
import com.nextuple.nsf.service.PrepService
import com.nextuple.nsf.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.net.NetworkInterface

@Module
@InstallIn(SingletonComponent::class)
internal object ServiceModule {

	// Other services should not be injected into this directly to avoid circular dependencies.
	// Instead, inject DTOs via setters as needed to add commonly logged properties.
	@Provides
	fun provideLogService(app: Application): LogService = LogService(app = app)

	@Provides
	fun provideDeviceService(
		app: Application,
		logService: LogService
	): DeviceService = DeviceService(
		logService = logService,
		getDeviceModel = { Build.MODEL },
		getDeviceId = @SuppressLint("HardwareIds") {
			Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
		},
		getMacAddress = {
			NetworkInterface.getNetworkInterfaces().toList().first {
				it.name.equals("wlan0", ignoreCase = true) && it.hardwareAddress != null
			}.let {
				it.hardwareAddress.joinToString { b -> String.format("%02X", b) }
			}
		}
	)

	@Provides
	fun provideAppService(
		appApi: AppApi,
		deviceService: DeviceService
	): AppService = AppService(appApi = appApi, deviceService = deviceService)

	@Provides
	fun provideUserService(
		userApi: UserApi,
		logService: LogService,
		deviceService: DeviceService
	): UserService = UserService(
		userApi = userApi,
		logService = logService,
		deviceService = deviceService
	)

	@Provides
	fun providePickService(
		pickApi: PickApi,
		deviceService: DeviceService
	): PickService = PickService(pickApi = pickApi, deviceService = deviceService)

	@Provides
	fun providePrepService(
		prepApi: PrepApi
	): PrepService = PrepService(prepApi = prepApi)

	@Provides
	fun provideOrderService(
		orderApi: OrderApi,
		deviceService: DeviceService
	): OrderService = OrderService(orderApi = orderApi, deviceService = deviceService)
}
