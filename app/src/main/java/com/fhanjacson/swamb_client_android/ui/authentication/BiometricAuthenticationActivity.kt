package com.fhanjacson.swamb_client_android.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityAuthenticateBinding
import java.util.concurrent.Executor

class BiometricAuthenticationActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthenticateBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupData()
        setupUI()
    }


    private fun setupData() {

    }

    private fun setupUI() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                toast("App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                toast("No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                toast("Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                toast("The user hasn't associated any biometric credentials with their account.")
        }

        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int,
                                                       errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(applicationContext,
                            "Authentication error: $errString", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(applicationContext,
                            "Authentication succeeded!", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(applicationContext, "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authenticate login for Facebook.com")
                .setSubtitle("Authorize login to Facebook.com for the user: myfacebookaccount31")
                .setDescription("Description")
                .setNegativeButtonText("Deny")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }
}