package com.nextuple.nsf.service.dto

import com.nextuple.nsf.service.dto.Brand.Companion.toBrand
import com.nextuple.nsf.service.dto.Brand.DSG
import com.nextuple.nsf.service.dto.Brand.FS
import com.nextuple.nsf.service.dto.Brand.GG
import com.nextuple.nsf.service.dto.Brand.PL
import com.nextuple.nsf.service.dto.Brand.UNKNOWN
import org.junit.Assert.assertEquals
import org.junit.Test

class BrandTest {

	@Test
	fun `toBrand should be case insensitive`() {
		val dsg = "dicks SpoRtIng Goods"
		val gg = "goLF galaXY"
		val pl = "pUbLiC Lands"
		val fs = "field and stream"

		assertEquals(DSG, toBrand(dsg))
		assertEquals(GG, toBrand(gg))
		assertEquals(PL, toBrand(pl))
		assertEquals(FS, toBrand(fs))
	}

	@Test
	fun `toBrand should return UNKNOWN for chain names that do not match anything`() {
		val rei = "rei lmao"

		assertEquals(UNKNOWN, toBrand(rei))
	}
}
