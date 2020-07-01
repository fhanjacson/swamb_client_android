package com.fhanjacson.swamb_client_android.service

import android.os.Build
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.model.FCMToken
import com.fhanjacson.swamb_client_android.ui.repository.FirestoreRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FCMService : FirebaseMessagingService() {

    private val repo = FirestoreRepository()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        logd("onMessageReceived")
        logd(Gson().toJson(message))

    }

    override fun onNewToken(refreshedToken: String) {
        super.onNewToken(refreshedToken)
        logd("onNewToken: $refreshedToken")

        if (refreshedToken.isNotEmpty()) {
            logd("Saving FCM Token to database ...")
            val fcmToken = FCMToken(token = refreshedToken, deviceName = "${Build.MANUFACTURER} ${Build.MODEL}")
            repo.saveFCMToken(fcmToken).addOnSuccessListener {
                logd("FCM Token saved to database")
            }.addOnFailureListener {
                Constant.loge("Fail to save FCM Token to database")
            }
        } else {
            logd("token is empty")
        }
    }


}
