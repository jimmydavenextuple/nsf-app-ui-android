package com.nextuple.nsf.ui.util

import java.net.InetSocketAddress
import java.net.Socket

class PrintUtil {
	companion object {
		const val PRINTER_PORT = 6101
		fun connectToSocket(ipAddress: String): Socket? {
			var socket: Socket? = null

			try {
				socket = Socket()
				socket.connect(InetSocketAddress(ipAddress, PRINTER_PORT), 1000)
			} catch (ignore: Exception) {
				// TODO: Don't ignore this exception - Log exception so it is not lost
				try {
					socket?.close()
				} catch (ignored: Exception) { }
			}
			// TODO: Refactor Errors here to make sure we pass through null socket or something on error instead of the socket we failed to connect to
			return socket
		}
	}
}

data class Printer(
	val printerName: String,
	var ipAddress: String = "",
	var connectionStatus: Boolean = false
)
enum class PrinterName {
	SFS, BOPIS, SDD, BOPL
}
