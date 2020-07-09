package com.fhanjacson.swamb_client_android.ui.authentication

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Base64.encodeToString
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityAuthenticateBinding
import java.security.KeyPair
import java.security.KeyStore
import java.security.Signature
import java.util.*
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

class BiometricAuthenticationActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthenticateBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var signatureResult: ByteArray
    private lateinit var keyPair: KeyPair

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupData()
        setupUI()
    }


    private fun setupData() {
//        keyPair = generateKeyPair()
        getSecretKey()
//        generateSecretKey(KeyGenParameterSpec.Builder(
//            Constant.KEY_ALIAS,
//            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
//            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//            .setUserAuthenticationRequired(true)
//            // Invalidate the keys if the user has registered a new biometric
//            // credential, such as a new fingerprint. Can call this method only
//            // on Android 7.0 (API level 24) or higher. The variable
//            // "invalidatedByBiometricEnrollment" is true by default.
////            .setInvalidatedByBiometricEnrollment(true)
//            .build())


    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }


    private fun getSecretKey(): KeyPair {
        val keyStore = KeyStore.getInstance(Constant.ANDROID_KEYSTORE)
        keyStore.load(null)
        val entry = keyStore.getEntry(Constant.DEVICE_KEYPAIR_ALIAS, null)
        val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val publicKey = keyStore.getCertificate(Constant.DEVICE_KEYPAIR_ALIAS).publicKey
        return KeyPair(publicKey, privateKey)
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
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
            binding.buttonSign.setOnClickListener {
//                signAction()
            }


        }
    }


//    fun signAction() {
//        executor = ContextCompat.getMainExecutor(this)
//        biometricPrompt = BiometricPrompt(this, executor,
//            object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationError(
//                    errorCode: Int,
//                    errString: CharSequence
//                ) {
//                    super.onAuthenticationError(errorCode, errString)
//                    Toast.makeText(
//                        applicationContext,
//                        "Authentication error: $errString", Toast.LENGTH_SHORT
//                    )
//                        .show()
//                }
//
//                override fun onAuthenticationSucceeded(
//                    result: BiometricPrompt.AuthenticationResult
//                ) {
//                    super.onAuthenticationSucceeded(result)
//                    Toast.makeText(
//                        applicationContext,
//                        "Authentication succeeded!", Toast.LENGTH_SHORT
//                    )
//                        .show()
//
////                        val encryptedInfo: ByteArray? = result.cryptoObject?.cipher?.doFinal(
////                            "hello world".toByteArray(Charset.defaultCharset())
////                        )
//
//                    val data = "hello world".toByteArray(Charset.defaultCharset())
//                    signData(data, result.cryptoObject?.signature)
//
//
//                }
//
//                override fun onAuthenticationFailed() {
//                    super.onAuthenticationFailed()
//                    Toast.makeText(
//                        applicationContext, "Authentication failed",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                }
//            })
//
//        promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Authenticate login for Facebook.com")
//            .setSubtitle("Authorize login to Facebook.com for the user: myfacebookaccount31")
//            .setDescription("Description")
//            .setNegativeButtonText("Deny")
//            .build()
//
//
////            val cipher = getCipher()
//        val sign = getSignature()
//        sign.initSign()
////            val secretKey = getSecretKey()
////            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
//        val data = "hello world".toByteArray(Charset.defaultCharset())
//
//
//        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(sign))
//    }



    fun signData(data: ByteArray, sign: Signature?) {

        signatureResult = sign?.run {
            update(data)
            sign()
        }!!

        val str_key = encodeToString(signatureResult, Base64.DEFAULT)

        logd("sign:\n$str_key")

//        if (signedMsg != null) {
//            //We encode and store in a variable the value of the signature
//            signatureResult = Base64.encodeToString(signedMsg, Base64.DEFAULT)
//            logd("Sign: $signResult")
//
//        }
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