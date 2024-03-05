package com.nextuple.nsf.datastore

import androidx.datastore.core.DataStore
import com.nextuple.nsf.UserData
import com.nextuple.nsf.service.dto.User
import com.nextuple.nsf.service.dto.toUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
	private val userDataStore: DataStore<UserData>
) {
	val userFlow: Flow<User> = userDataStore.data.map { it.toUser() }

	suspend fun updateUser(firstName: String, lastName: String, dks: String) {
		userDataStore.updateData {
			it.toBuilder()
				.setFirstName(firstName)
				.setLastName(lastName)
				.setDks(dks)
				.build()
		}
	}

	suspend fun clearUser() {
		userDataStore.updateData {
			it.toBuilder().clear().build()
		}
	}

	suspend fun getDks(): String? = userDataStore.data.firstOrNull()?.dks
}
