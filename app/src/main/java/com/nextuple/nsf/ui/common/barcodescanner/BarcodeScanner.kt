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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

@Composable
fun BarcodeScanner(
    hasScanned: Boolean,
    expectedUpc: String,
    onResult: (String) -> Unit,
    onCancel: () -> Unit,
) {
    Log.i("BarcodeScanner","BarcodeScanner: upc: $expectedUpc");
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

    CameraPreview(
        onImageCaptured = { imageProxy ->
            if (!hasScanned) {
                processImageProxy(barcodeScanner, imageProxy, expectedUpc, context, hasShownToast, onResult)
            } else {
                imageProxy.close() // Close the image to prevent memory leaks
            }
        },
        onCancel = onCancel
    )
}

@Composable
fun CameraPreview(
    onImageCaptured: (ImageProxy) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create a PreviewView for showing the camera preview
    val previewView = remember { PreviewView(context) }

    // Create and configure the camera provider and camera selector
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Set up a coroutine scope to initialize CameraX
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
            // Bind the preview and image analysis use cases to the lifecycle
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

    // Draw a viewfinder overlay
    EnhancedViewfinderOverlay()
}

@Composable
fun EnhancedViewfinderOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
    ) {
        Box(
            modifier = Modifier
                .size(280.dp, 500.dp) // 80% of screen width, adjustable size
                .border(
                    width = 4.dp,
                    color = Color(0xFFFFFFFF), // White border with opacity
                    shape = RoundedCornerShape(24.dp) // More rounded corners for a modern look
                )
                .background(Color(0x20000000))
                .align(Alignment.Center)
        ) {
            // Add crosshair with enhanced styling
            EnhancedCrossHair()
        }

        // Optional: Add some text overlay for user guidance
        Text(
            text = "Align the barcode within the frame",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(0.8f), // Slightly transparent
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 16.sp)
        )
    }
}

@Composable
fun EnhancedCrossHair() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Padding to give more breathing room for crosshair
    ) {
        val lineThickness = 3.dp.toPx()
        val lineLength = size.minDimension * 0.25f // Make the lines shorter for a cleaner look

        // Draw horizontal crosshair line
        drawLine(
            color = Color.White.copy(alpha = 0.8f), // Semi-transparent white
            start = Offset((size.width - lineLength) / 2, size.height / 2),
            end = Offset((size.width + lineLength) / 2, size.height / 2),
            strokeWidth = lineThickness,
            cap = StrokeCap.Round // Rounded ends for the lines
        )

        // Draw vertical crosshair line
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
                        imageProxy.close() // Close the current image to avoid memory leak
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
@androidx.compose.ui.tooling.preview.Preview
private fun ViewfinderOverlayPreview() {
    EnhancedViewfinderOverlay()
}


