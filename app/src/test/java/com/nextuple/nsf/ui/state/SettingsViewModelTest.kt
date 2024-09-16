package com.nextuple.nsf.ui.state

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.nextuple.nsf.CoroutineRule
import com.nextuple.nsf.Printers
import com.nextuple.nsf.datastore.PrintersRepository
import com.nextuple.nsf.service.WifiService
import com.nextuple.nsf.ui.util.GenericViewState
import com.nextuple.nsf.ui.util.Printer
import com.nextuple.nsf.ui.util.PrinterName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

	@get:Rule
	val coroutineRule = CoroutineRule()

	private lateinit var vm: SettingsViewModel

	@Suppress("unused")
	@MockK
	private lateinit var savedStateHandle: SavedStateHandle

	@MockK
	private lateinit var wifiService: WifiService

	@MockK
	private lateinit var printersRepository: PrintersRepository

	private var isDebug = true

	private val BASE_IP = "198.68.72."

	@Before
	fun setUp() {
		MockKAnnotations.init(this)

		mockkStatic(Log::class)
		every { Log.i(any(), any()) } returns 0
		every { Log.e(any(), any()) } returns 0

		every { wifiService.getIpAddressFromDevice() } returns "${BASE_IP}12"

		justRun { runBlocking { printersRepository.savePrinters(any()) } }

		vm = SettingsViewModel(
			isDebug,
			wifiService,
			StandardTestDispatcher(),
			printersRepository,
			savedStateHandle
		)
	}

	@Test
	fun `bypassPrinter initial value = build debug value`() {
		assertEquals(isDebug, vm.bypassPrinter)
	}

	@Test
	fun `toggleBypassPrinter correctly toggles bypassPrinter`() {
		assertTrue(vm.bypassPrinter)
		vm.toggleBypassPrinter()
		assertFalse(vm.bypassPrinter)
	}

	@Test
	fun `setIpPrefix should get ipAddress from wifiService`() {
		vm.setIpPrefix()

		verify { wifiService.getIpAddressFromDevice() }
		assertEquals(BASE_IP, vm.ipPrefix)
	}

	@Test
	fun `connect printer should update printer list on success`() = runTest {
		val ipAddress = "198.68.72.12"
		val printerType = PrinterName.BOPL
		val sddPrinter = vm.findPrinter(printerType)
		assertEquals(false, sddPrinter.connectionStatus)

		vm.connectPrinter(sddPrinter, ipAddress)
		advanceUntilIdle()

		val updatedPrinter = vm.findPrinter(printerType)
		assertEquals(true, updatedPrinter.connectionStatus)
		assertEquals(ipAddress, updatedPrinter.ipAddress)
	}

	@Test
	fun `connect printer should not update printer list on error`() = runTest {
		val ipAddress = "198.68.72.12"
		val printerType = PrinterName.BOPL
		val sddPrinter = vm.findPrinter(printerType)
		assertEquals(false, sddPrinter.connectionStatus)
		// Test with isDebug set to false
		vm = SettingsViewModel(
			false,
			wifiService,
			StandardTestDispatcher(),
			printersRepository,
			savedStateHandle
		)

		vm.connectPrinter(sddPrinter, ipAddress)
		advanceUntilIdle()

		val updatedPrinter = vm.findPrinter(printerType)
		assertEquals(false, updatedPrinter.connectionStatus)
	}

	@Test
	fun `disconnect printer should disconnect entered printer`() = runTest {
		val sfsPrinter = vm.findPrinter(PrinterName.SFS)
		assertEquals(true, sfsPrinter.connectionStatus)

		vm.disConnectPrinter(sfsPrinter)
		advanceUntilIdle()

		val updatedPrinter = vm.findPrinter(PrinterName.SFS)
		assertEquals(false, updatedPrinter.connectionStatus)
	}

	@Test
	fun `resetConnectionState should set state to Idle`() {
		vm.resetConnectionState()

		assertEquals(GenericViewState.Idle, vm.printerConnectionState)
	}

	@Test
	fun `retrieveSavedPrinters should get printers and set them correctly`() = runTest {
		val printersListInFlow = Printers.newBuilder()
			.addPrinterList(
				com.nextuple.nsf.Printer.newBuilder()
					.setPrinterName("SFS")
					.setIpAddress("10.0.0.2")
					.setConnectionStatus(true)
					.build()
			)
			.addPrinterList(
				com.nextuple.nsf.Printer.newBuilder()
					.setPrinterName("BOPIS")
					.setIpAddress("10.0.0.2")
					.setConnectionStatus(true)
					.build()
			)
			.addPrinterList(
				com.nextuple.nsf.Printer.newBuilder()
					.setPrinterName("SDD")
					.setIpAddress("10.0.0.2")
					.setConnectionStatus(true)
					.build()
			)
			.build()
		val printerResult = listOf(
			Printer(printerName = "SFS", ipAddress = "10.0.0.2", connectionStatus = true),
			Printer(printerName = "BOPIS", ipAddress = "10.0.0.2", connectionStatus = true),
			Printer(printerName = "SDD", ipAddress = "10.0.0.2", connectionStatus = true),
			Printer(printerName = "BOPL", connectionStatus = false)
		)
		val flowToReturn: Flow<Printers> = flow {
			emit(printersListInFlow)
		}
		coEvery { printersRepository.getPrinters() } returns flowToReturn

		vm.retrieveSavedPrinters()
		advanceUntilIdle()

		assertEquals(printerResult, vm.printersList)
	}
}
