package com.fhanjacson.swamb_client_android

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.view.View
import androidx.biometric.BiometricManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityMainBinding
import com.fhanjacson.swamb_client_android.model.AuthenticationData
import com.fhanjacson.swamb_client_android.model.InitDeviceRequest
import com.fhanjacson.swamb_client_android.model.InitDeviceResponse
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.fhanjacson.swamb_client_android.repository.SharedPreferencesRepository
import com.fhanjacson.swamb_client_android.ui.authentication.BiometricAuthenticationActivity
import com.fhanjacson.swamb_client_android.ui.onboarding.OnboardingActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import java.security.KeyPair
import java.security.KeyPairGenerator

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notifHelper: NotificationHelper
    private val auth = FirebaseAuth.getInstance()
    private val bRepo = BackendRepository()
    private lateinit var preference: SharedPreferencesRepository
    private lateinit var currentUser: FirebaseUser

    private var debugVarForceSignout = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        preference = SharedPreferencesRepository(this)
        val view = binding.root
        setContentView(view)

        if (auth.currentUser != null) {
            currentUser = auth.currentUser!!
            setupData(currentUser)
            setupUI(currentUser)

            if (intent != null && intent.action != null) {
                processIntent(intent)
            }
        } else {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            auth.signOut()
            preference.clearAll()
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }

        logd("MainActivity.Intent.action: ${intent.action}")
        logd("MainActivity.Intent.extras.INTENT_PARAM_AUTH_DATA: ${Gson().toJson(intent.getSerializableExtra(Constant.INTENT_PARAM_AUTH_DATA))}")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        logd("MainActivity.onNewIntent.Intent.action: ${intent?.action}")
        logd("MainActivity.onNewIntent.Intent.extras.INTENT_PARAM_AUTH_DATA: ${Gson().toJson(intent?.getSerializableExtra(Constant.INTENT_PARAM_AUTH_DATA))}")

        if (intent != null && intent.action != null) {
            processIntent(intent)
        }
    }

    private fun processIntent(intent: Intent) {
        when (intent.action) {
            Constant.INTENT_ACTION_AUTH -> {
                val openAuthActivity = Intent(this, BiometricAuthenticationActivity::class.java).apply {
                    val authData = intent.getSerializableExtra(Constant.INTENT_PARAM_AUTH_DATA) as AuthenticationData
                    putExtra(Constant.INTENT_PARAM_AUTH_DATA, authData)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                intent.removeExtra(Constant.INTENT_PARAM_AUTH_DATA)
                startActivity(openAuthActivity)

            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    override fun onStart() {
        super.onStart()
        updateUI(currentUser)
    }

    private fun setupData(currentUser: FirebaseUser) {
        notifHelper = NotificationHelper(this)
        logd("Current User ID: ${currentUser.uid}")
        val isDeviceInit = preference.isDeviceInit
        if (isDeviceInit == null) {
            preference.isDeviceInit = false
            initDevice(currentUser)
        } else {
            if (!isDeviceInit) {
                initDevice(currentUser)
            }
        }
    }

    private fun setupUI(currentUser: FirebaseUser) {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_linkage,
                R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        binding.loadingBar.setOnClickListener {
            if (debugVarForceSignout >= 5) {
                auth.signOut()
                preference.clearAll()
                toast("signout")
                debugVarForceSignout = 0
            } else {
                debugVarForceSignout++
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser) {

    }


    private fun generateKeyPair(): KeyPair {
        logd("Generating KeyPair")
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, Constant.ANDROID_KEYSTORE
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            Constant.DEVICE_KEYPAIR_ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256)
            setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            setUserAuthenticationRequired(true)
            build()
        }
        kpg.initialize(parameterSpec)
        val keyPair = kpg.generateKeyPair()
        return keyPair
    }
//        logd("Public Key Length: ${Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT).length}")
//        preference.isKeyPairGenerated = true

    private fun savePublicKey() {
        logd("Saving Public Key")
    }

    private fun initDevice(currentUser: FirebaseUser) {
        logd("initDevice")
        binding.loadingText.text = "Init Device, please wait..."
        binding.loadingLayout.visibility = View.VISIBLE

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val fcmToken = it.token
            if (fcmToken.isNotEmpty()) {
                preference.fcmToken = fcmToken
                val keyPair = generateKeyPair()
                val devicePublicKey = Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT)
                if (devicePublicKey.isNotEmpty()) {
                    val initDeviceRequest = InitDeviceRequest(
                        userID = currentUser.uid,
                        fcmToken = fcmToken,
                        deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
                        devicePublicKey = devicePublicKey
                    )
                    bRepo.initDevice(initDeviceRequest).responseObject(InitDeviceResponse.Deserializer()) { req, res, initDeviceResult ->
                        initDeviceResult.fold(success = { data ->
                            if (data.success) {
                                preference.isDeviceInit = true
                                preference.deviceID = data.deviceID
                                logd("Success to init Device")
                            } else {
                                loge("Fail to init Device")
                            }
                        }, failure = { error ->
                            toast("Fail to init Device")
                            loge("Fail to init Device")
                            loge(error.toString())
                        })
                        binding.loadingLayout.visibility = View.GONE
                    }
                }
            } else {
                loge("Public Key is empty")
            }
        }
    }
}