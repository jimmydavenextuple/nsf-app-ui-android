package com.nextuple.nsf.util

object StringUtils {
	fun toInitials(firstName: String, lastName: String): String =
		"${toInitial(firstName)}${toInitial(lastName)}"

	private fun toInitial(name: String): String = name.firstOrNull()?.uppercase().orEmpty()

	fun toLastNameFirstInitial(name: String): String {
		val firstName = name.split(" ")[0]
		val lastName = name.split(" ")[name.split(" ").lastIndex]
		return "$lastName, ${toInitial(firstName)}."
	}

	fun toPhoneNumberFormatted(phoneNumber: String): String {
		val numbersOnly = phoneNumber.filter { it.isDigit() }
		if (numbersOnly.length != 10) return phoneNumber

		return runCatching {
			Regex("(\\d{3})(\\d{3})(\\d{4})").replace(numbersOnly, "($1) $2 - $3")
		}.getOrDefault(phoneNumber)
	}

	fun isValidIPv4Address(input: String): Boolean {
		val pattern =
			Regex("^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$")
		return pattern.matches(input)
	}

	fun splitAreaAndBin(holdingLocation: String): Pair<String, String> {
		val binStartIndex = holdingLocation.indexOf("Bin", ignoreCase = true)
		if (binStartIndex < 0) {
			return holdingLocation to ""
		}

		val area = holdingLocation.substring(0, binStartIndex).trim()
		val bin = holdingLocation.substring(binStartIndex).trim()
		return area to bin
	}
}
