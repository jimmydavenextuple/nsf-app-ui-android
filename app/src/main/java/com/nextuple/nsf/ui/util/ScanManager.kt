package com.nextuple.nsf.ui.util

typealias OnDataScanned = (data: String, symbology: String?) -> Unit

interface ScanManager {
	fun set(onDataScanned: OnDataScanned)
}

/**
 * Mainly for Compose previews.
 */
class NoOpScanManager : ScanManager {
	override fun set(onDataScanned: OnDataScanned) {
		// No-op.
	}
}
