package com.nextuple.nsf.ui.state

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.datastore.PrintersRepository
import com.nextuple.nsf.hilt.IoDispatcher
import com.nextuple.nsf.service.WifiService
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.security.SecureRandom
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val isDebug: Boolean,
	private val wifiService: WifiService,
	@IoDispatcher
	private val ioDispatcher: CoroutineDispatcher,
	private val printersRepository: PrintersRepository,
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle
) : ViewModel() {

	companion object {
		private const val PRINTER_PORT = 6101
		private const val PLACEHOLDER_IP_PREFIX = "192.0.1."
	}

	var ipPrefix: String? by mutableStateOf(null)
		private set

	var printerConnectionState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	private var bypassPrinter: Boolean by mutableStateOf(BuildConfig.BYPASS_PRINTER)
		private set

	private val defaultPrinter = Printer(
		printerName = PrinterName.SFS.name,
		connectionStatus = false
	)

	var holdSlipPrintState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	private var _printersList = mutableListOf(
		defaultPrinter,
		Printer(
			printerName = PrinterName.BOPIS.name,
			connectionStatus = false
		),
		Printer(
			printerName = PrinterName.SDD.name,
			connectionStatus = false
		),
		Printer(
			printerName = PrinterName.BOPL.name,
			connectionStatus = false
		)
	)

	var printersList: List<Printer>? by mutableStateOf(_printersList)

	init {
		if (isDebug) {
			setupFakePrinters()
		}
	}

	fun setIpPrefix() {
		val wifiIp = wifiService.getIpAddressFromDevice()
		ipPrefix = wifiIp.substring(0, wifiIp.lastIndexOf(".") + 1)
	}

	private fun setupFakePrinters() {
		findPrinter(PrinterName.SFS).apply {
			ipAddress = getIpAddress()
			connectionStatus = true
		}

		findPrinter(PrinterName.BOPIS).apply {
			ipAddress = getIpAddress()
			connectionStatus = true
		}
	}

	/**
	 * Used to setup fake printers. DEV ONLY
	 */
	private fun getIpAddress(): String = "$PLACEHOLDER_IP_PREFIX${SecureRandom().nextInt(256)}"

	fun findPrinter(printerName: PrinterName) = _printersList.find { it.printerName == printerName.name } ?: defaultPrinter

	fun connectPrinter(printer: Printer, ipAddress: String) {
		printerConnectionState = GenericViewState.Loading
		CoroutineScope(ioDispatcher).launch {
			var socket: Socket? = null

			try {
				if (!(isDebug)) {
					delay(1000)
					socket = Socket()
					socket.connect(InetSocketAddress(ipAddress, PRINTER_PORT), 1000)
				}

				val index = printersList?.indexOf(printer)
				if ((index?.compareTo(0) ?: 0) >= 0) {
					_printersList[index ?: 0] = printer.copy(
						connectionStatus = !printer.connectionStatus,
						ipAddress = ipAddress
					)
				}
				printersList = _printersList
				savePrintersToDevice()
				printerConnectionState = GenericViewState.Success

				// TODO: Toast instead of logging here
				Log.i("printerConnectSuccess", "Successfully Connected to Printer")
			} catch (ignore: Exception) {
				// If printer does not connect, display a failure and toast that there was an error
				printerConnectionState = GenericViewState.Failure
				Log.e("printerConnectError", "Issue Connecting to Printer, Issue: " + ignore)
			} finally {
				// Close the socket after confirming printer connectivity and saving it
				try {
					socket?.close()
				} catch (ignored: Exception) { }
			}
		}
	}

	fun printBOPISHoldSlip(holdSlipZPL: MutableList<String>) {
		holdSlipPrintState = GenericViewState.Loading
		CoroutineScope(ioDispatcher).launch {
			runCatching {
				if (holdSlipZPL.isEmpty()) { throw Exception() }
				connectToSocket(findPrinter(PrinterName.BOPIS).ipAddress)?.use { socket ->
					if (bypassPrinter) {
						holdSlipZPL.forEach { pageZPL ->
							Log.i("HoldSlipZPL Printout", "pageZPL = $pageZPL")
						}
					} else {
						PrintWriter(socket.getOutputStream(), true).use {
							holdSlipZPL.forEach { pageZPL ->
								it.write(pageZPL)
							}
						}
					}
				} ?: {
					Log.e("holdSlipPrintError", "Issue Connecting to Printer")
					holdSlipPrintState = GenericViewState.Failure
				}
			}.onSuccess {
				Log.i("holdSlipPrintSuccess", "Successfully printed BOPIS Hold Slip")
				holdSlipPrintState = GenericViewState.Success
			}.onFailure {
				Log.e("nullHoldSlip", "Hold Slip not Printed: hold slip info not found", it)
				holdSlipPrintState = GenericViewState.Failure
			}
		}
	}

	fun disConnectPrinter(printer: Printer) {
		printerConnectionState = GenericViewState.Loading
		viewModelScope.launch {
			val index = printersList?.indexOf(printer)
			if ((index?.compareTo(0) ?: 0) >= 0) {
				_printersList[index ?: 0] = printer.copy(connectionStatus = !printer.connectionStatus)
			}
			printersList = _printersList
			savePrintersToDevice()
			printerConnectionState = GenericViewState.Success
		}
	}

	fun resetConnectionState() {
		printerConnectionState = GenericViewState.Idle
	}

	/**
	 * Removing existing holdSlipPrint state and default to default state.
	 */
	fun resetHoldSlipPrintState() {
		holdSlipPrintState = GenericViewState.Idle
	}

	fun toggleBypassPrinter(): Boolean {
		bypassPrinter = !bypassPrinter

		return bypassPrinter
	}

	private fun connectToSocket(ipAddress: String): Socket? {
		var socket: Socket? = null

		try {
			socket = Socket()
			socket.connect(InetSocketAddress(ipAddress, PRINTER_PORT), 1000)
		} catch (ignore: Exception) {
			try {
				socket?.close()
			} catch (ignored: Exception) { }
		}
		return socket
	}

	fun retrieveSavedPrinters() = viewModelScope.launch {
		val savedPrinterList: MutableList<com.nextuple.nsf.Printers> = mutableListOf()
		printersRepository.getPrinters().collect { printers ->
			savedPrinterList.add(printers)
			_printersList.forEach { printer ->
				// Check for each printer instead of fully overwriting in case saved data does not exist
				val foundSavedPrinter =
					savedPrinterList.first().printerListList.find { it.printerName == printer.printerName }
				val newPrinter = foundSavedPrinter?.let {
					Printer(
						printerName = it.printerName,
						ipAddress = it.ipAddress,
						connectionStatus = it.connectionStatus
					)
				}
				if (newPrinter != null) {
					_printersList[_printersList.indexOf(printer)] = newPrinter
				}
			}
			printersList = _printersList
			cancel("")
		}
	}

	private fun savePrintersToDevice() = viewModelScope.launch {
		val printersToSave: MutableList<com.nextuple.nsf.Printer> = mutableListOf()
		printersList?.forEach {
			printersToSave.add(
				com.nextuple.nsf.Printer.newBuilder()
					.setPrinterName(it.printerName)
					.setIpAddress(it.ipAddress)
					.setConnectionStatus(it.connectionStatus)
					.build()
			)
		}
		printersRepository.savePrinters(printersToSave)
	}
}

// todo: cleanup, only use one printer class
data class Printer(
	val printerName: String,
	var ipAddress: String = "",
	var connectionStatus: Boolean = false
)
enum class PrinterName {
	SFS, BOPIS, SDD, BOPL
}
