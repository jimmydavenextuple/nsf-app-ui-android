package com.nextuple.nsf.hilt

import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.retrofit.ApiCallAdapterFactory
import com.nextuple.nsf.retrofit.api.ConfigApi
import com.nextuple.nsf.retrofit.api.InfoApi
import com.nextuple.nsf.retrofit.api.OrderApi
import com.nextuple.nsf.retrofit.api.PackTaskApi
import com.nextuple.nsf.retrofit.api.PickApi
import com.nextuple.nsf.retrofit.api.StageTaskApi
import com.nextuple.nsf.retrofit.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
internal object RetrofitModule {

	@Provides
	fun provideRetrofit(): Retrofit {
		val interceptor = HttpLoggingInterceptor()
		if (BuildConfig.DEBUG) {
			interceptor.level = HttpLoggingInterceptor.Level.BODY
		}
		val builder = OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.MINUTES)
			.readTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(30, TimeUnit.SECONDS).addInterceptor(interceptor).build()

		return Retrofit.Builder()
			.baseUrl(BuildConfig.GOAT_API_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(ApiCallAdapterFactory())
			.client(builder)
			.build()
	}

	@Provides
	fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

	@Provides
	fun providePickApi(retrofit: Retrofit): PickApi = retrofit.create(PickApi::class.java)

	@Provides
	fun provideAppApi(retrofit: Retrofit): InfoApi = retrofit.create(InfoApi::class.java)

	@Provides
	fun providePrepApi(retrofit: Retrofit): PackTaskApi = retrofit.create(PackTaskApi::class.java)

	@Provides
	fun provideOrderApi(retrofit: Retrofit): OrderApi = retrofit.create(OrderApi::class.java)

	@Provides
	fun provideStageTaskApi(retrofit: Retrofit): StageTaskApi = retrofit.create(StageTaskApi::class.java)

	@Provides
	fun provideConfigApi(retrofit: Retrofit): ConfigApi = retrofit.create(ConfigApi::class.java)
}
