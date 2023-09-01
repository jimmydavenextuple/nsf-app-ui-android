package com.nextuple.nsf.ui.util

typealias OnDataScanned = (data: String, symbology: String?) -> Unit

val PICK_ITEM_SYMBOLOGY_PREFIXES = setOf(
	"upc",
	"ean",
	"code39",
	"code128"
)

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
