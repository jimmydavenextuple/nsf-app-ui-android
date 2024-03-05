package com.nextuple.nsf.service.dto

import com.nextuple.nsf.UserData
import com.nextuple.nsf.service.dto.Brand.UNKNOWN

data class User(
	val firstName: String = "",
	val lastName: String = "",
	val dks: String = "",
	val store: Store = Store(id = "0", brand = UNKNOWN)
) {
	fun getFullName() = "$firstName $lastName".trim()

	fun getInitials() = runCatching { "${firstName.first()}${lastName.first()}" }.getOrNull()

	fun isLoggedIn() = dks.isNotEmpty()
}

fun UserData.toUser() = User(
	firstName = this.firstName,
	lastName = this.lastName,
	dks = this.dks
	// Todo: Find way to implement store
)
