package com.fhanjacson.swamb_client_android.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Base64.encodeToString
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityAuthenticateBinding
import com.fhanjacson.swamb_client_android.model.AuthenticationData
import com.fhanjacson.swamb_client_android.model.BackendResponse
import com.fhanjacson.swamb_client_android.model.ValidateAuthenticationRequest
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.google.gson.Gson
import java.lang.Exception
import java.nio.charset.Charset
import java.security.KeyPair
import java.security.KeyStore
import java.security.Signature
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import kotlin.math.sign

class BiometricAuthenticationActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthenticateBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var signatureResult: ByteArray
    private lateinit var keyPair: KeyPair
    private val bRepo = BackendRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupData()
        setupUI()

//        logd("BiometricAuthenticationActivity.Intent.action: ${intent?.action}")
//        logd("BiometricAuthenticationActivity.Intent.extras.INTENT_PARAM_AUTH_DATA: ${Gson().toJson(intent?.getSerializableExtra(Constant.INTENT_PARAM_AUTH_DATA))}")
    }


//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        logd("BiometricAuthenticationActivity.onNewIntent.Intent.action: ${intent?.action}")
//        logd("BiometricAuthenticationActivity.onNewIntent.Intent.extras.INTENT_PARAM_AUTH_DATA: ${Gson().toJson(intent?.getSerializableExtra(Constant.INTENT_PARAM_AUTH_DATA))}")
//    }

    private fun setupData() {

    }

    private fun getSecretKey(): KeyPair {
        val keyStore = KeyStore.getInstance(Constant.ANDROID_KEYSTORE)
        keyStore.load(null)
        val entry = keyStore.getEntry(Constant.DEVICE_KEYPAIR_ALIAS, null)
        val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val publicKey = keyStore.getCertificate(Constant.DEVICE_KEYPAIR_ALIAS).publicKey
        return KeyPair(publicKey, privateKey)
    }

    private fun setupUI() {
        val authDataNullable = intent.getSerializableExtra(Constant.INTENT_PARAM_AUTH_DATA) as AuthenticationData?

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

            if (authDataNullable != null) {
                val authData: AuthenticationData = authDataNullable
                logd("authData: ${Gson().toJson(authData)}")


                try {
                    val iatLong = authData.iat.toLong()
                    val expLong = authData.exp.toLong()

                    val iatDate = Date(iatLong)
                    val expDate = Date(expLong)

                    binding.authIat.text = "Authentication Requested at: ${Constant.SIMPLE_DATE_FORMAT.format(iatDate)}"
                    binding.authExp.text = "Authentication Expires at: ${Constant.SIMPLE_DATE_FORMAT.format(expDate)}"
                } catch (e: Exception) {
                    loge(e.message.toString())
                }

                binding.vendorName.text = authData.vendorName
                binding.vendorUserID.text = authData.vendorUserID

                binding.fingerprintButton.setOnClickListener {
                    signAction(authData)
                }
            } else {
                MaterialDialog(this).show {
                    title(text = "ERROR")
                    message(text = "This page is launched without the necessary extras")
                    positiveButton {
                        finish()
                    }
                    cancelable(false)
                    cancelOnTouchOutside(false)
                }
            }
        }
    }


    fun signAction(authData: AuthenticationData) {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    loge("auth errorCode: $errorCode")
                    loge("auth errString: $errString")
                    toast("Authentication error:$errString")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    toast("Authentication succeeded")

                    val data = authData.randomString.toByteArray(Charset.defaultCharset())
                    signData(data, result.cryptoObject?.signature, authData)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    toast("Authentication failed")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("SWAMB Authentication request from ${authData.vendorName}")
            .setSubtitle("Authenticate account: ${authData.vendorUserID} on ${authData.vendorName}")
            .setDescription("By confirming your fingerprint, you are allowing an authenticate request from ${authData.vendorName}")
            .setNegativeButtonText("Deny Authentication")
            .build()

        val sign = getSignature()
        sign.initSign(getSecretKey().private)
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(sign))
    }


    fun signData(data: ByteArray, sign: Signature?, authData: AuthenticationData) {
        signatureResult = sign?.run {
            update(data)
            sign()
        }!!
        val signedString = encodeToString(signatureResult, Base64.DEFAULT)
        logd("signed:\n$signedString")


        try {
            val linkageID = authData.linkageID.toInt()
            val validateAuthenticationRequest = ValidateAuthenticationRequest(signedString, linkageID)

            bRepo.validateAuthentication(validateAuthenticationRequest)
                .responseObject(BackendResponse.Deserializer()) { req, res, validateAuthenticationResult ->
                    validateAuthenticationResult.fold(success = { data ->
                        logd(data.message)
                        if (data.success) {
                            toast("Authentication Success")
                            logd("Authentication Success")
                            finish()
                        }
                    }, failure = { error ->
                        toast("Authentication Fail")
                        loge("Authentication Fail")
                        loge(error.toString())
                        finish()
                    })
                }
        } catch (e: Exception) {
            loge(e.toString())
        }
    }

    private fun getSignature(): Signature {
        return Signature.getInstance("SHA256withRSA")
    }

    private fun getAlias(): Enumeration<String> {
        val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val aliases: Enumeration<String> = ks.aliases()
        return aliases
    }

}