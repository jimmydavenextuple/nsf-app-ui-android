package com.nextuple.nsf.hilt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.nextuple.nsf.Printers
import com.nextuple.nsf.UserData
import com.nextuple.nsf.datastore.PrintersRepository
import com.nextuple.nsf.datastore.UserRepository
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatastoreModule {

	private val Context.printersDataStore: DataStore<Printers> by dataStore(
		fileName = "Printers.pb",
		serializer = PrintersSerializer
	)

	@Provides
	@Reusable
	fun provideProtoDataStore(@ApplicationContext context: Context) = context.printersDataStore

	@Provides
	@Reusable
	internal fun providesDataRepository(
		printersDataStore: DataStore<Printers>
	): PrintersRepository {
		return PrintersRepository(printersDataStore)
	}

	private val Context.userDataStore: DataStore<UserData> by dataStore(
		fileName = "User.pb",
		serializer = UserSerializer
	)

	@Provides
	@Reusable
	fun provideUserDataStore(@ApplicationContext context: Context) = context.userDataStore

	@Provides
	@Reusable
	internal fun providesUserRepository(
		userDataStore: DataStore<UserData>
	): UserRepository {
		return UserRepository(userDataStore)
	}
}
