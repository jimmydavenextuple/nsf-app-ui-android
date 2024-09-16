package com.nextuple.nsf.service.dto

import com.google.protobuf.Timestamp
import com.nextuple.nsf.UserData
import com.nextuple.nsf.service.dto.Brand.UNKNOWN

data class User(
	val firstName: String = "",
	val lastName: String = "",
	val userId: String = "",
	val store: Store = Store(id = "0", brand = UNKNOWN),
	val lastActiveTime: Timestamp = Timestamp.getDefaultInstance()
) {
	fun getFullName() = "$firstName $lastName".trim()

	fun getInitials() = runCatching { "${firstName.first()}${lastName.first()}" }.getOrNull()
}

fun UserData.toUser() = User(
	firstName = this.firstName,
	lastName = this.lastName,
	userId = this.userId,
	lastActiveTime = this.lastActiveTime
	// Todo: Find way to implement store
)
