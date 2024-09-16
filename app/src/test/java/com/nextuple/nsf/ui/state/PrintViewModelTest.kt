package com.nextuple.nsf.ui.state

import android.util.Base64
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.ui.util.GenericViewState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrintViewModelTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	@InjectMockKs
	private lateinit var vm: PrintViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var ioDispatcher: CoroutineDispatcher

	@Before
	fun setUp() {
		MockKAnnotations.init(this)

		mockkStatic(Log::class)
		every { Log.i(any(), any()) } returns 0
		every { Log.e(any(), any()) } returns 0
	}

	@Test
	fun `printHoldSlip should successfully handle valid hold slip data`() = runTest {
		mockkStatic(Base64::class)
		every { Base64.decode(any<String>(), Base64.DEFAULT) } returns byteArrayOf()

		val holdSlipList = mutableListOf("hold", "slip", "zpl")
		vm.printHoldSlip(holdSlipList, TEST_PRINTER, true)
		advanceUntilIdle()

		verify(exactly = holdSlipList.size) {
			Log.i(PrintViewModel.TAG_ZPL, any())
		}

		verify(exactly = 1) {
			Log.i(PrintViewModel.TAG, "Successfully printed Hold Slip")
		}

		Assert.assertEquals(GenericViewState.Success, vm.printHoldSlipState)
	}

	@Test
	fun `printHoldSlip should set state to Failure when an exception is thrown`() = runTest {
		mockkStatic(Base64::class)
		every { Log.e(any(), any(), any()) } returns 0

		val holdSlipList = mutableListOf<String>()
		vm.printHoldSlip(holdSlipList, TEST_PRINTER, false)
		advanceUntilIdle()

		verify(exactly = 1) {
			Log.e(any(), any(), any())
		}

		Assert.assertEquals(GenericViewState.Failure, vm.printHoldSlipState)
	}

	@Test
	fun `resetHoldSlipPrintState should set state to Idle`() {
		vm.resetHoldSlipPrintState()

		Assert.assertEquals(GenericViewState.Idle, vm.printHoldSlipState)
	}

	companion object {
		private val TEST_PRINTER = com.nextuple.nsf.ui.util.Printer(
			printerName = "BOPIS",
			ipAddress = "100.10.123",
			connectionStatus = true
		)
	}
}
