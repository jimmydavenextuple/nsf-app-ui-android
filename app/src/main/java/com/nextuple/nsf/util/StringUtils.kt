package com.nextuple.nsf.util

object StringUtils {
	fun toInitials(firstName: String, lastName: String): String =
		"${toInitial(firstName)}${toInitial(lastName)}"

	private fun toInitial(name: String): String = name.firstOrNull()?.uppercase().orEmpty()
}
