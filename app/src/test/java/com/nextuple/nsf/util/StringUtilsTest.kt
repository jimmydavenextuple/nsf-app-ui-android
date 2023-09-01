package com.nextuple.nsf.util

import com.nextuple.nsf.util.StringUtils.toInitials
import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsTest {
	@Test
	fun `toInitials, with full name, should convert to uppercase initials`() {
		val first = "abc DEF"
		val last = "ghijkl-"

		val res = toInitials(first, last)

		assertEquals("AG", res)
	}

	@Test
	fun `toInitials, with only first name, should convert to uppercase first initial`() {
		val first = "abc DEF"
		val last = ""

		val res = toInitials(first, last)

		assertEquals("A", res)
	}

	@Test
	fun `toInitials, with only last name, should convert to uppercase last initial`() {
		val first = ""
		val last = "ghijkl-"

		val res = toInitials(first, last)

		assertEquals("G", res)
	}

	@Test
	fun `toInitials, with blank name, should return empty string`() {
		val res = toInitials("", "")

		assertEquals("", res)
	}
}
