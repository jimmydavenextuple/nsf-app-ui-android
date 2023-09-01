package com.nextuple.nsf.hilt

import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.retrofit.ApiCallAdapterFactory
import com.nextuple.nsf.retrofit.api.AppApi
import com.nextuple.nsf.retrofit.api.OrderApi
import com.nextuple.nsf.retrofit.api.PickApi
import com.nextuple.nsf.retrofit.api.PrepApi
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
	fun provideAppApi(retrofit: Retrofit): AppApi = retrofit.create(AppApi::class.java)

	@Provides
	fun providePrepApi(retrofit: Retrofit): PrepApi = retrofit.create(PrepApi::class.java)

	@Provides
	fun provideOrderApi(retrofit: Retrofit): OrderApi = retrofit.create(OrderApi::class.java)
}
