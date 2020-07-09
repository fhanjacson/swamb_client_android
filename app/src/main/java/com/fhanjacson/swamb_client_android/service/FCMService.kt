package com.fhanjacson.swamb_client_android.service

import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.model.BackendResponse
import com.fhanjacson.swamb_client_android.model.UpdateFCMTokenRequest
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.fhanjacson.swamb_client_android.repository.FirestoreRepository
import com.fhanjacson.swamb_client_android.repository.SharedPreferencesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FCMService : FirebaseMessagingService() {

    private val bRepo = BackendRepository()
    private var auth = FirebaseAuth.getInstance()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        logd("onMessageReceived")
        logd(Gson().toJson(message))

    }

    override fun onNewToken(refreshedToken: String) {
        super.onNewToken(refreshedToken)
        logd("onNewToken: $refreshedToken")
        logd("token length: ${refreshedToken.length}")

        val preferences = SharedPreferencesRepository(this)

        if (refreshedToken.isNotEmpty()) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val updateFCMTokenRequest = UpdateFCMTokenRequest(currentUser.uid, refreshedToken)
                bRepo.updateFCMToken(updateFCMTokenRequest).responseObject(BackendResponse.Deserializer()) { req, res, updateFCMTokenResult ->
                    updateFCMTokenResult.fold(success = { data ->
                        logd(Gson().toJson(data))
                        if (data.status == 200) {
                            Constant.loge("Update FCM Token Success")
                        }
                    }, failure = { error ->
                        Constant.loge("Fail to update FCM Token")
                        Constant.loge(error.toString())
                    })

                }
            } else {
                preferences.fcmToken = refreshedToken
            }
        } else {
            logd("token is empty")
        }
    }


}
