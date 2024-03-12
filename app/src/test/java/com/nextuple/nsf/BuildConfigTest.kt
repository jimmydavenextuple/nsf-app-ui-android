package com.nextuple.nsf

import com.nextuple.nsf.service.dto.Brand.NT
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Test

class BuildConfigTest {

	@Test
	fun `AUTO_FILL_DKS should be false`() {
		assertFalse(BuildConfig.AUTO_FILL_DKS)
	}

	@Test
	fun `BYPASS_PRINTER should be false`() {
		assertFalse(BuildConfig.BYPASS_PRINTER)
	}
}
