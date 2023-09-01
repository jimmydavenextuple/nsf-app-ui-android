package com.nextuple.nsf.ui.state

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextuple.nsf.BuildConfig
import com.nextuple.nsf.ui.screen.settings.Printer
import com.nextuple.nsf.ui.util.GenericViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.security.SecureRandom
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	@Suppress("UNUSED_PARAMETER")
	handler: SavedStateHandle
) : ViewModel() {

	companion object {
		private const val PRINTER_PORT = 6101
	}

	var ipPrefix: String? by mutableStateOf("192.0.1.")
		private set

	var printerConnectionState: GenericViewState by mutableStateOf(GenericViewState.Idle)
		private set

	private var _printersList = mutableListOf(
		Printer(
			printerName = "SFS",
			ipAddress = getIpAddress(),
			connectionStatus = true
		),
		Printer(
			printerName = "BOPIS",
			ipAddress = getIpAddress(),
			connectionStatus = false
		),
		Printer(
			printerName = "SDD",
			ipAddress = getIpAddress(),
			connectionStatus = false
		)
	)

	var printersList: List<Printer>? by mutableStateOf(_printersList)

	private fun getIpAddress(): String {
		val random = SecureRandom()
		return "192.168.0.${random.nextInt(256)}"
	}

	fun connectPrinter(printer: Printer, ipAddress: String) {
		printerConnectionState = GenericViewState.Loading
		viewModelScope.launch {
			var socket: Socket? = null

			try {
				if (!(BuildConfig.DEBUG)) {
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
				printerConnectionState = GenericViewState.Success

				// TODO: Toast instead of logging here
				Log.i("printerConnectSuccess", "Successfully Connected to Printer")
			} catch (ignore: Exception) {
				// If printer does not connect, display a failure and toast that there was an error
				printerConnectionState = GenericViewState.Failure
				Log.e("printerConnectError", "Issue Connecting to Printer")
			} finally {
				// Close the socket after confirming printer connectivity and saving it
				try {
					socket?.close()
				} catch (ignored: Exception) { }
			}
		}
	}

	fun printBOPISHoldSlip(holdSlipZPL: MutableList<String>) {
		connectToSocket(printersList!!.find { it.printerName == "BOPIS" }!!.ipAddress)?.use { socket ->
			holdSlipZPL.forEach { pageZPL ->
				val printerCode = String(
					Base64.decode(
						pageZPL,
						Base64.DEFAULT
					)
				)
				if (!(BuildConfig.DEBUG)) {
					PrintWriter(socket.getOutputStream(), true).use {
						it.write(printerCode)
					}
				} else {
					Log.i("HoldSlipZPL Printout", "pageZPL = $pageZPL")
				}

				Log.i("holdSlipPrintSuccess", "Successfully printed BOPIS Hold Slip")
				// TODO: Replace these Logs with Toasts
			}
		} ?: Log.e("holdSlipPrintError", "Issue Connecting to Printer: Make sure the IP is set correctly in Settings")
	}

	fun disConnectPrinter(printer: Printer) {
		printerConnectionState = GenericViewState.Loading
		viewModelScope.launch {
			val index = printersList?.indexOf(printer)
			if ((index?.compareTo(0) ?: 0) >= 0) {
				_printersList[index ?: 0] = printer.copy(connectionStatus = !printer.connectionStatus)
			}
			printersList = _printersList
			printerConnectionState = GenericViewState.Success
		}
	}

	fun resetConnectionState() {
		printerConnectionState = GenericViewState.Idle
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
}
