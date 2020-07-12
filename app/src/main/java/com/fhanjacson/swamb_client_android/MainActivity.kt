package com.fhanjacson.swamb_client_android

import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityMainBinding
import com.fhanjacson.swamb_client_android.model.BackendResponse
import com.fhanjacson.swamb_client_android.model.InitDeviceRequest
import com.fhanjacson.swamb_client_android.model.InitDeviceResponse
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.fhanjacson.swamb_client_android.repository.SharedPreferencesRepository

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
        logd("isDeviceInit: $isDeviceInit")

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
                R.id.navigation_dashboard,
                R.id.navigation_notifications
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
//        logd("Public Key Length: ${Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT).length}")
//        preference.isKeyPairGenerated = true
    }

    private fun savePublicKey() {
        logd("Saving Public Key")
    }

    private fun initDevice(currentUser: FirebaseUser) {

        logd("initDevice")
        binding.loadingText.text = "Init Device, please wait..."
        binding.loadingLayout.visibility = View.VISIBLE

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val fcmToken = it.token
            logd("fcmToken: $fcmToken")
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
                    logd("initDevice(initDeviceRequest)")
                    bRepo.initDevice(initDeviceRequest).responseObject(InitDeviceResponse.Deserializer()) { req, res, initDeviceResult ->
                        initDeviceResult.fold(success = { data ->
                            logd(Gson().toJson(data))
                            if (data.status == 200) {
                                preference.isDeviceInit = true
                                preference.deviceID = data.deviceID
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
                logd("lol")
            }
        }

    }

//    private fun saveFCMToken(currentUser: FirebaseUser) {
//        notifHelper.getFCMToken()
//            .addOnSuccessListener { result ->
//                val token = result.token
//                if (token.isNotEmpty()) {
//                    logd("Saving FCM Token to database ...")
//                    val fcmToken = FCMToken(token = token, deviceName = "${Build.MANUFACTURER} ${Build.MODEL}")
//                    fRepo.saveFCMToken(fcmToken).addOnSuccessListener {
//                        logd("FCM Token saved to database")
//                    }.addOnFailureListener {
//                        toast("Fail to save FCM Token to database")
//                        loge(it.toString())
//                    }
//                } else {
//                    logd("token is empty")
//                }
//            }.addOnFailureListener {
//                toast("failed to get InstanceID token")
//                loge(it.toString())
//            }
//    }
}