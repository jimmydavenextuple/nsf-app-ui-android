package com.nextuple.nsf.service.dto

import com.nextuple.nsf.service.dto.Brand.UNKNOWN

data class User(
	val firstName: String = "",
	val lastName: String = "",
	val dks: String = "",
	val store: Store = Store(id = "0", brand = UNKNOWN)
)
