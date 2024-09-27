package com.nextuple.nsf.ui.screen.barcodescanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextuple.nsf.ui.common.barcodescanner.BarcodeScanner
import com.nextuple.nsf.ui.state.BarcodeScannerViewModel

@Composable
fun BarcodeScannerScreen(
    viewModel: BarcodeScannerViewModel = hiltViewModel(),
    upc: String,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val cameraPermissionGranted = remember { mutableStateOf(false) }

    val hasScanned by remember { mutableStateOf(false) }

    // Permission launcher to request camera permission
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraPermissionGranted.value = true
            } else {
                Toast.makeText(context, "Camera permission is required to scan barcodes", Toast.LENGTH_SHORT).show()
                onCancel()
            }
        }
    )

    // Check if permission is already granted
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        cameraPermissionGranted.value = true
    } else {
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Show BarcodeScanner only if permission is granted
    if (cameraPermissionGranted.value) {
        BarcodeScanner(
            hasScanned = hasScanned,
            expectedUpc = upc,
            onResult = { barcode ->
                Log.i("TESTPOP", "popupto")
                viewModel.onBarcodeScanned(barcode)
            },
            onCancel = {
                onCancel()
            }
        )
    }
}
