package com.nextuple.nsf.service

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint

class BarcodeScanManager {

    private var scannedBarcode = StringBuilder()
//    private var isScanning = false
    var onBarcodeScanned: ((String) -> Unit)? = null

    // Starts the scanning process
    fun clearScan() {
        scannedBarcode.clear()
//        isScanning = true
    }

    // Stops the scanning process
    private fun stopScan() {
//        isScanning = false
    }

    // Handles key events (HID input)
    fun handleKeyEvent(keyEvent: KeyEvent): Boolean {
//        if (!isScanning) return false

        if (keyEvent.type == KeyEventType.KeyDown) {
            val char = keyEvent.utf16CodePoint.toChar()
            if (char.isLetterOrDigit()) {
                scannedBarcode.append(char)
            }

            if (keyEvent.key == Key.Enter) {
                onBarcodeScanned?.invoke(scannedBarcode.toString())
                clearScan()
            }
            return true
        }
        return false
    }
}