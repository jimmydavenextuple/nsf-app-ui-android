package com.nextuple.nsf

import junit.framework.TestCase.assertFalse
import org.junit.Test

class BuildConfigTest {

	@Test
	fun `AUTO_FILL_USER_ID should be false`() {
		assertFalse(BuildConfig.AUTO_FILL_USER_ID)
	}

	@Test
	fun `BYPASS_PRINTER should be false`() {
		assertFalse(BuildConfig.BYPASS_PRINTER)
	}
}
