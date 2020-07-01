package com.fhanjacson.swamb_client_android

import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityMainBinding
import com.fhanjacson.swamb_client_android.model.FCMToken
import com.fhanjacson.swamb_client_android.ui.repository.FirestoreRepository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notifHelper: NotificationHelper
    private val auth = FirebaseAuth.getInstance()
    private val repo = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupData()
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            saveFCMToken(currentUser)
            updateUI(currentUser)
        }
    }

    private fun setupData() {
        notifHelper = NotificationHelper(this)
    }

    private fun setupUI() {
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

    }

    private fun updateUI(currentUser: FirebaseUser) {

    }

    private fun saveFCMToken(currentUser: FirebaseUser) {
        notifHelper.getFCMToken()
            .addOnSuccessListener { result ->
                val token = result.token
                if (token.isNotEmpty()) {
                    logd("Saving FCM Token to database ...")
                    val fcmToken = FCMToken(token = token, deviceName = "${Build.MANUFACTURER} ${Build.MODEL}")
                    repo.saveFCMToken(fcmToken).addOnSuccessListener {
                        logd("FCM Token saved to database")
                    }.addOnFailureListener {
                        toast("Fail to save FCM Token to database")
                        loge(it.toString())
                    }
                } else {
                    logd("token is empty")
                }
            }.addOnFailureListener {
                toast("failed to get InstanceID token")
                loge(it.toString())
            }
    }
}