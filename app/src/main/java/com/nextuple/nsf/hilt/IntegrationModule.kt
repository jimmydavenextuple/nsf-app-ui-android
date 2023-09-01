package com.nextuple.nsf.hilt

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Vibrator
import com.nextuple.nsf.ui.util.Haptics
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object IntegrationModule {

	@Provides
	fun provideDataWedgeBroadcastReceiver(): DataWedgeBroadcastReceiver =
		DataWedgeBroadcastReceiver()

	@Provides
	fun provideHaptics(vibrator: Vibrator): Haptics = Haptics(vibrator = vibrator)

	@Provides
	fun provideVibrator(app: Application): Vibrator {
		val serviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			Context.VIBRATOR_MANAGER_SERVICE
		} else {
			Context.VIBRATOR_SERVICE
		}
		return app.getSystemService(serviceName) as Vibrator
	}
}
