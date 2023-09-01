package com.nextuple.nsf.util

import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver.Companion.INTENT_ACTION
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver.Companion.INTENT_CATEGORY
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver.Companion.INTENT_DATA_KEY
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver.Companion.INTENT_LABEL_KEY
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver.Companion.INTENT_SRC
import com.nextuple.nsf.util.DataWedgeBroadcastReceiver.Companion.INTENT_SRC_KEY
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class DataWedgeBroadcastReceiverTest {

	private val dataWedge = DataWedgeBroadcastReceiver()

	@MockK(relaxed = true)
	private lateinit var ctxWrapper: ContextWrapper

	@MockK(relaxed = true)
	private lateinit var intent: Intent

	@Before
	fun setUp() {
		MockKAnnotations.init(this)

		mockkConstructor(IntentFilter::class)
		justRun { anyConstructed<IntentFilter>().addAction(any()) }
		justRun { anyConstructed<IntentFilter>().addCategory(any()) }

		// Tests confirming successful invocation should override this.
		dataWedge.setOnDataScanned { _, _ -> fail("unexpected invocation") }
	}

	@Test
	fun `registerSelf should gracefully handle null context wrapper`() {
		dataWedge.registerSelf(null)
	}

	@Test
	fun `registerSelf should register with the expected intent filter`() {
		dataWedge.registerSelf(ctxWrapper)

		verify {
			ctxWrapper.registerReceiver(refEq(dataWedge), any())
			anyConstructed<IntentFilter>().addAction(eq(INTENT_ACTION))
			anyConstructed<IntentFilter>().addCategory(eq(INTENT_CATEGORY))
		}
	}

	@Test
	fun `unregisterSelf should gracefully handle null context wrapper`() {
		dataWedge.unregisterSelf(null)
	}

	@Test
	fun `unregisterSelf should unregister`() {
		dataWedge.unregisterSelf(ctxWrapper)

		verify {
			ctxWrapper.unregisterReceiver(refEq(dataWedge))
		}
	}

	@Test
	fun `onReceive should gracefully handle null context`() {
		dataWedge.onReceive(null, mockk())
	}

	@Test
	fun `onReceive should gracefully handle null intent`() {
		dataWedge.onReceive(mockk(), null)
	}

	@Test
	fun `onReceive should ignore non-matching actions`() {
		every { intent.action } returns "wrongAction"

		dataWedge.registerSelf(ctxWrapper)
		dataWedge.onReceive(mockk(), intent)

		verify {
			intent.action
		}
		confirmVerified(intent)
	}

	@Test
	fun `onReceive should ignore non-matching sources`() {
		every { intent.action } returns INTENT_ACTION
		every { intent.getStringExtra(INTENT_SRC_KEY) } returns "wrongSrc"

		dataWedge.registerSelf(ctxWrapper)
		dataWedge.onReceive(mockk(), intent)

		verify {
			intent.action
			intent.getStringExtra(INTENT_SRC_KEY)
		}
		confirmVerified(intent)
	}

	@Test
	fun `onReceive gracefully handle null sources`() {
		val expectedData = "testData"

		every { intent.action } returns INTENT_ACTION
		every { intent.getStringExtra(INTENT_SRC_KEY) } returns null
		every { intent.getStringExtra(INTENT_DATA_KEY) } returns expectedData
		every { intent.getStringExtra(INTENT_LABEL_KEY) } returns null

		var wasCalled = false
		dataWedge.setOnDataScanned { data, symbology ->
			assertEquals(expectedData, data)
			assertNull(symbology)
			wasCalled = true
		}
		dataWedge.registerSelf(ctxWrapper)
		dataWedge.onReceive(mockk(), intent)

		verify {
			intent.action
			intent.getStringExtra(INTENT_SRC_KEY)
			intent.getStringExtra(INTENT_DATA_KEY)
			intent.getStringExtra(INTENT_LABEL_KEY)
		}
		assertTrue(wasCalled)
	}

	@Test
	fun `onReceive should ignore empty data`() {
		every { intent.action } returns INTENT_ACTION
		every { intent.getStringExtra(INTENT_SRC_KEY) } returns INTENT_SRC
		every { intent.getStringExtra(INTENT_DATA_KEY) } returns ""

		dataWedge.registerSelf(ctxWrapper)
		dataWedge.onReceive(mockk(), intent)

		verify {
			intent.action
			intent.getStringExtra(INTENT_SRC_KEY)
			intent.getStringExtra(INTENT_DATA_KEY)
		}
		confirmVerified(intent)
	}

	@Test
	fun `onReceive should ignore null data`() {
		every { intent.action } returns INTENT_ACTION
		every { intent.getStringExtra(INTENT_SRC_KEY) } returns INTENT_SRC
		every { intent.getStringExtra(INTENT_DATA_KEY) } returns null

		dataWedge.registerSelf(ctxWrapper)
		dataWedge.onReceive(mockk(), intent)

		verify {
			intent.action
			intent.getStringExtra(INTENT_SRC_KEY)
			intent.getStringExtra(INTENT_DATA_KEY)
		}
		confirmVerified(intent)
	}

	@Test
	fun `onReceive gracefully handle unexpected label types`() {
		val expectedData = "testData"

		every { intent.action } returns INTENT_ACTION
		every { intent.getStringExtra(INTENT_SRC_KEY) } returns INTENT_SRC
		every { intent.getStringExtra(INTENT_DATA_KEY) } returns expectedData
		every { intent.getStringExtra(INTENT_LABEL_KEY) } returns "badLabel"

		var wasCalled = false
		dataWedge.setOnDataScanned { data, symbology ->
			assertEquals(expectedData, data)
			assertNull(symbology)
			wasCalled = true
		}
		dataWedge.registerSelf(ctxWrapper)
		dataWedge.onReceive(mockk(), intent)

		verify {
			intent.action
			intent.getStringExtra(INTENT_SRC_KEY)
			intent.getStringExtra(INTENT_DATA_KEY)
			intent.getStringExtra(INTENT_LABEL_KEY)
		}
		assertTrue(wasCalled)
	}

	@Test
	fun `onReceive gracefully handle null label types`() {
		val expectedData = "testData"

		every { intent.action } returns INTENT_ACTION
		every { intent.getStringExtra(INTENT_SRC_KEY) } returns INTENT_SRC
		every { intent.getStringExtra(INTENT_DATA_KEY) } returns expectedData
		every { intent.getStringExtra(INTENT_LABEL_KEY) } returns null

		var wasCalled = false
		dataWedge.setOnDataScanned { data, symbology ->
			assertEquals(expectedData, data)
			assertNull(symbology)
			wasCalled = true
		}
		dataWedge.registerSelf(ctxWrapper)
		dataWedge.onReceive(mockk(), intent)

		verify {
			intent.action
			intent.getStringExtra(INTENT_SRC_KEY)
			intent.getStringExtra(INTENT_DATA_KEY)
			intent.getStringExtra(INTENT_LABEL_KEY)
		}
		assertTrue(wasCalled)
	}

	@Test
	fun `onReceive handle parsing valid label types into symbology`() {
		val expectedData = "testData"
		val expectedSymbology = "symbology"
		val label = "LABEL-TYPE-$expectedSymbology"

		every { intent.action } returns INTENT_ACTION
		every { intent.getStringExtra(INTENT_SRC_KEY) } returns INTENT_SRC
		every { intent.getStringExtra(INTENT_DATA_KEY) } returns expectedData
		every { intent.getStringExtra(INTENT_LABEL_KEY) } returns label

		var wasCalled = false
		dataWedge.setOnDataScanned { data, symbology ->
			assertEquals(expectedData, data)
			assertEquals(expectedSymbology, symbology)
			wasCalled = true
		}
		dataWedge.registerSelf(ctxWrapper)
		dataWedge.onReceive(mockk(), intent)

		verify {
			intent.action
			intent.getStringExtra(INTENT_SRC_KEY)
			intent.getStringExtra(INTENT_DATA_KEY)
			intent.getStringExtra(INTENT_LABEL_KEY)
		}
		assertTrue(wasCalled)
	}
}
