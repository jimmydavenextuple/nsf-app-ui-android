package com.nextuple.nsf.ui.common.barcodescanner

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.nextuple.nsf.ui.util.PreviewPdtNordOne

@Composable
fun BarcodeScanner(
    hasScanned: Boolean,
    expectedUpc: String,
    onResult: (String) -> Unit,
    onCancel: () -> Unit,
) {
    Log.i("BarcodeScanner","BarcodeScanner: upc: $expectedUpc")
    val hasShownToast = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_ALL_FORMATS,
            Barcode.FORMAT_QR_CODE,
        )
        .build()
    val barcodeScanner = BarcodeScanning.getClient(options)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)).clickable { onCancel() },
        contentAlignment = Alignment.Center
    ) {
        // Main content box with 60% height
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            // Close icon at the top right corner
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clickable { onCancel() },
                tint = Color.Black
            )

            // Camera preview
            CameraPreview(
                onImageCaptured = { imageProxy ->
                    if (!hasScanned) {
                        processImageProxy(barcodeScanner, imageProxy, expectedUpc, context, hasShownToast, onResult)
                    } else {
                        imageProxy.close()
                    }
                },
                onCancel = onCancel
            )

            // Viewfinder overlay
            EnhancedViewfinderOverlay()
        }

        Spacer(modifier = Modifier.padding(top = 20.dp))

        Text(
            text = "Align the barcode within the frame",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
        )
        Text(
            text = "Tap to cancel",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 16.sp)
        )
    }
}

@Composable
fun CameraPreview(
    onImageCaptured: (ImageProxy) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val previewUseCase = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    onImageCaptured(imageProxy)
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageAnalysisUseCase
            )
        } catch (e: Exception) {
            Log.e("CameraPreview", "Use case binding failed", e)
            onCancel()
        }
    }

    // Render the camera preview in the Composable
    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun EnhancedViewfinderOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 4.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            CrossHair()
        }
    }
}

@Composable
fun CrossHair() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val lineThickness = 3.dp.toPx()
        val lineLength = size.minDimension * 0.25f

        // Draw horizontal line
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset((size.width - lineLength) / 2, size.height / 2),
            end = Offset((size.width + lineLength) / 2, size.height / 2),
            strokeWidth = lineThickness,
            cap = StrokeCap.Round
        )

        // Draw vertical line
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(size.width / 2, (size.height - lineLength) / 2),
            end = Offset(size.width / 2, (size.height + lineLength) / 2),
            strokeWidth = lineThickness,
            cap = StrokeCap.Round
        )
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    expectedUpc: String,
    context: android.content.Context,
    hasShownToast: MutableState<Boolean>,
    onBarcodeScanned: (String) -> Unit,
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { scannedUpc ->
                        if (scannedUpc == expectedUpc) {
                            onBarcodeScanned(scannedUpc)
                        } else {
                            if (!hasShownToast.value) {
                                Toast.makeText(
                                    context,
                                    "UPC Mismatch: Expected $expectedUpc, Found $scannedUpc",
                                    Toast.LENGTH_SHORT
                                ).show()
                                hasShownToast.value = true

                                // Reset the toast flag after 3 seconds
                                Handler(Looper.getMainLooper()).postDelayed({
                                    hasShownToast.value = false
                                }, 3000)
                            }
                        }
                        imageProxy.close()
                        return@addOnSuccessListener
                    }
                }
            }
            .addOnFailureListener {
                Log.e("BarcodeScanner", "Failed to scan barcode", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

@Composable
@PreviewPdtNordOne
private fun BarcodeScannerPreview() {
    BarcodeScanner(
        hasScanned = false,
        expectedUpc = "123456789012",
        onResult = {},
        onCancel = {}
    )
}