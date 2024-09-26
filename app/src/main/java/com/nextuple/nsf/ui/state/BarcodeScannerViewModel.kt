package com.nextuple.nsf.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BarcodeScannerViewModel : ViewModel() {
    var scannedBarcode by mutableStateOf<String?>(null)

    fun onBarcodeScanned(result: String) {
        scannedBarcode = result
    }

    fun reset() {
        scannedBarcode = null
    }
}