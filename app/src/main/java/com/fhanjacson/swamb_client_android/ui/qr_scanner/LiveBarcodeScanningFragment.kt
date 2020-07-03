package com.fhanjacson.swamb_client_android.ui.qr_scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentLiveBarcodeScanningBinding
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.github.kittinunf.fuel.Fuel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class LiveBarcodeScanningFragment : BaseFragment() {

    private var _binding: FragmentLiveBarcodeScanningBinding? = null
    private val binding get() = _binding!!

    private lateinit var preview: Preview
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveBarcodeScanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        permissionsBuilder(
            Manifest.permission.CAMERA
        ).build().send { result ->
            if (!result.allGranted()) {
                MaterialDialog(requireActivity()).show {
                    title(text = "Camera permission required")
                    message(text = "This feature requires camera permission")
                    positiveButton {
                        findNavController().navigateUp()
                    }
                    cancelable(false)
                    cancelOnTouchOutside(false)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionsBuilder(
            Manifest.permission.CAMERA
        ).build().send { result ->
            if (result.allGranted()) {
                setupUI()
                updateCameraUI()
                setupCamera()
            } else {
                MaterialDialog(requireActivity()).show {
                    title(text = "Camera permission required")
                    message(text = "This feature requires camera permission")
                    positiveButton {
                        findNavController().navigateUp()
                    }
                    cancelable(false)
                    cancelOnTouchOutside(false)
                }
            }
        }

    }

    private fun setupUI() {
        binding.viewDimBackground.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun updateCameraUI() {
        binding.buttonSwitchCamera.setOnClickListener {
            lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            // Re-bind use cases to update selected camera
            bindCameraUseCase()
        }
    }

    private fun setupCamera() {
        logd("SETUP CAMERA")
        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Enable or disable switching between cameras
            updateCameraSwitchButton()

            // Build and bind the camera use cases
            bindCameraUseCase()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindCameraUseCase() {
        val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }

        logd("Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

//        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
//        logd("Preview aspect ratio: $screenAspectRatio")

        val rotation = binding.viewFinder.display.rotation

        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                    if (barcode.isNotEmpty()) {
                        if(barcode.startsWith("swamb:")) {
                            binding.viewDimBackground.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.VISIBLE
                            cameraProvider.unbindAll()
                            logd("Barcode Found: $barcode")
                            toast("barcode: $barcode")
                            addAccount(barcode)
                        } else {
                            toast("Not a valid SWAMB QR code")
                        }
                    }
                })
            }

        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())
        } catch (exc: Exception) {
            Constant.loge("Use case binding failed \n $exc")
        }
    }

    private fun addAccount(qrCode: String) {
        Fuel.get("https://reqres.in/api/users/2?delay=3")
            .response { request, response, result ->
                result.fold(success = {
                    val action = LiveBarcodeScanningFragmentDirections.actionLiveBarcodeScanningFragmentToAddAccountFragment()
                    findNavController().navigate(action)

                }, failure = {
                    MaterialDialog(requireActivity()).show {
                        title(text = "Fail to add account")
                        message(text = it.localizedMessage)
                        positiveButton {
                            bindCameraUseCase()
                            binding.viewDimBackground.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                        }
                        cancelable(false)
                        cancelOnTouchOutside(false)
                    }
                })

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Shut down our background executor
        cameraExecutor.shutdown()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Redraw the camera UI controls
        updateCameraUI()
        // Enable or disable switching between cameras
        updateCameraSwitchButton()
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /** Enabled or disabled a button to switch cameras depending on the available cameras */
    private fun updateCameraSwitchButton() {
        try {
            binding.buttonSwitchCamera.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            binding.buttonSwitchCamera.isEnabled = false
        }
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }
}