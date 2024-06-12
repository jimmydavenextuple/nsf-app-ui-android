package com.nextuple.nsf.hilt

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.provider.Settings
import com.nextuple.nsf.datastore.UserRepository
import com.nextuple.nsf.retrofit.api.ConfigApi
import com.nextuple.nsf.retrofit.api.InfoApi
import com.nextuple.nsf.retrofit.api.OrderApi
import com.nextuple.nsf.retrofit.api.PackTaskApi
import com.nextuple.nsf.retrofit.api.PickApi
import com.nextuple.nsf.retrofit.api.StageTaskApi
import com.nextuple.nsf.retrofit.api.UserApi
import com.nextuple.nsf.service.ConfigService
import com.nextuple.nsf.service.DeviceService
import com.nextuple.nsf.service.InfoService
import com.nextuple.nsf.service.LogService
import com.nextuple.nsf.service.OrderService
import com.nextuple.nsf.service.PackTaskService
import com.nextuple.nsf.service.PickService
import com.nextuple.nsf.service.StageTaskService
import com.nextuple.nsf.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.net.NetworkInterface
import javax.inject.Singleton

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
	fun provideUserService(
		userApi: UserApi,
		logService: LogService,
		userRepository: UserRepository
	): UserService = UserService(
		userApi = userApi,
		logService = logService,
		userRepository = userRepository
	)

	@Provides
	fun providePickService(
		pickApi: PickApi,
		logService: LogService,
		userRepository: UserRepository
	): PickService = PickService(
		pickApi = pickApi,
		logService = logService,
		userRepository = userRepository
	)

	@Provides
	fun providePrepService(
		packTaskApi: PackTaskApi,
		logService: LogService,
		userRepository: UserRepository
	): PackTaskService = PackTaskService(
		packTaskApi = packTaskApi,
		logService = logService,
		userRepository = userRepository
	)

	@Provides
	fun provideAppService(
		infoApi: InfoApi,
		userRepository: UserRepository
	): InfoService = InfoService(
		infoApi = infoApi,
		userRepository = userRepository
	)

	@Provides
	fun provideOrderService(
		orderApi: OrderApi,
		userRepository: UserRepository
	): OrderService = OrderService(
		orderApi = orderApi,
		userRepository = userRepository
	)

	@Provides
	fun provideStageTaskService(
		stageTaskApi: StageTaskApi,
		logService: LogService,
		userRepository: UserRepository
	): StageTaskService = StageTaskService(
		stageTaskApi = stageTaskApi,
		logService = logService,
		userRepository = userRepository
	)

	@Singleton
	@Provides
	fun provideConfigService(
		configApi: ConfigApi,
		logService: LogService,
		userRepository: UserRepository
	): ConfigService = ConfigService(
		configApi = configApi,
		logService = logService,
		userRepository = userRepository
	)
}
