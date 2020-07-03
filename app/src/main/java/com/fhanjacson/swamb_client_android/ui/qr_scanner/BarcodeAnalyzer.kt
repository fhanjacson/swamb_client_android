package com.fhanjacson.swamb_client_android.ui.qr_scanner

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream

typealias BarcodeListener = (barcodeResult: String) -> Unit

class BarcodeAnalyzer(listener: BarcodeListener? = null) : ImageAnalysis.Analyzer {

    private val listeners = ArrayList<BarcodeListener>().apply { listener?.let { add(it) } }
    private lateinit var imageProxy: ImageProxy


    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        if (listeners.isEmpty()) {
            image.close()
            return
        }

        val mediaImage = image.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, 0)
            imageProxy = image
//            listeners.forEach{ it(mediaImage.toBitmap()) }

            scanBarcodges(inputImage)
                .addOnSuccessListener { barcodes ->
//                    logd("Success to decode barcode")
//                    logd(Gson().toJson(barcodes))

                    for (barcode in barcodes) {
//                        val bounds = barcode.boundingBox
//                        val corners = barcode.cornerPoints

                        val rawValue = barcode.rawValue

                        if (rawValue != null) {
//                            logd("barcode: $rawValue")
                            listeners.forEach { it(rawValue) }
                        } else {
                            listeners.forEach { it("") }
                        }
                    }
                    imageProxy.close()

                }
                .addOnFailureListener {
//                listeners.forEach { it("") }
                    loge("Fail to decode barcode")
                    it.printStackTrace()
                    imageProxy.close()
                }
        }
    }

    private fun scanBarcodges(image: InputImage): Task<MutableList<Barcode>> {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()

        val scanner = BarcodeScanning.getClient(options)
        return scanner.process(image)

    }

    fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}
