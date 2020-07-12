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
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentLiveBarcodeScanningBinding
import com.fhanjacson.swamb_client_android.model.CreateLinkageRequest
import com.fhanjacson.swamb_client_android.model.CreateLinkageResponse
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.fhanjacson.swamb_client_android.repository.SharedPreferencesRepository
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.firebase.auth.FirebaseAuth
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
    private var auth = FirebaseAuth.getInstance()
    private var bRepo = BackendRepository()
    private lateinit var preference: SharedPreferencesRepository


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
        preference = SharedPreferencesRepository(requireActivity())
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
    }

    private fun updateCameraUI() {
        binding.buttonSwitchCamera.setOnClickListener {
            lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            bindCameraUseCase()
        }
    }

    private fun setupCamera() {
        logd("SETUP CAMERA")
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }
            updateCameraSwitchButton()
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
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { qrCodeString ->
                    val swambPrefix = "swamb:"
                    if (qrCodeString.isNotEmpty()) {
                        if(qrCodeString.startsWith(swambPrefix)) {
                            val token = qrCodeString.removePrefix(swambPrefix)
                            binding.loadingLayout.visibility = View.VISIBLE
                            cameraProvider.unbindAll()
                            logd("Barcode Found: $qrCodeString")
                            toast("Processing QR Code...")
                            initLinkage(token)
                        } else {
                            toast("Not a valid SWAMB QR code")
                        }
                    }
                })
            }
        cameraProvider.unbindAll()
        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )
            preview.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())
        } catch (exc: Exception) {
            Constant.loge("Use case binding failed \n $exc")
        }
    }

    private fun initLinkage(token: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val deviceID = preference.deviceID
            if (deviceID != null && deviceID > -1) {
                val createLinkageRequest = CreateLinkageRequest(token, currentUser.uid, deviceID)
                bRepo.createLinkage(createLinkageRequest).responseObject(CreateLinkageResponse.Deserializer()) { req, res, createLinkageResult ->
                    createLinkageResult.fold(success = { data ->
                        if (data.linkageResult) {
                            MaterialDialog(requireActivity()).show {
                                title(text = "Linkage Success")
                                message(text = data.message)
                                positiveButton {
                                    findNavController().navigateUp()
                                }
                                cancelable(false)
                                cancelOnTouchOutside(false)
                            }
                            binding.loadingLayout.visibility = View.GONE
                        } else {
                            MaterialDialog(requireActivity()).show {
                                title(text = "Linkage Fail")
                                message(text = data.message)
                                positiveButton {
                                    findNavController().navigateUp()
                                }
                                cancelable(false)
                                cancelOnTouchOutside(false)
                            }
                            binding.loadingLayout.visibility = View.GONE
                            loge("Linkage Fail Cause Linkage already Exist")
                        }
                    }, failure = { error ->
                        toast("Fail to Create Linkage")
                        loge("Fail to Create Linkage")
                        loge(error.toString())
                        binding.loadingLayout.visibility = View.GONE

                    })
                }
            }
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