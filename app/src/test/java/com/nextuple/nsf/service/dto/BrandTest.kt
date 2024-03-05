package com.nextuple.nsf.service.dto

import com.nextuple.nsf.service.dto.Brand.Companion.toBrand
import com.nextuple.nsf.service.dto.Brand.UNKNOWN
import org.junit.Assert.assertEquals
import org.junit.Test

class BrandTest {

	@Test
	fun `toBrand should be case insensitive`() {
		val nextuple = "NextUple"
		assertEquals(Brand.NT, toBrand(nextuple))
	}

	@Test
	fun `toBrand should return UNKNOWN for chain names that do not match anything`() {
		val rei = "rei lmao"

		assertEquals(UNKNOWN, toBrand(rei))
	}
}
