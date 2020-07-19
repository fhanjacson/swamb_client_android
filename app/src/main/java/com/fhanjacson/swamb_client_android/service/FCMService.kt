package com.fhanjacson.swamb_client_android.service

import android.app.PendingIntent
import android.content.Intent
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.MainActivity
import com.fhanjacson.swamb_client_android.R
import com.fhanjacson.swamb_client_android.model.AuthenticationData
import com.fhanjacson.swamb_client_android.model.BackendResponse
import com.fhanjacson.swamb_client_android.model.UpdateFCMTokenRequest
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.fhanjacson.swamb_client_android.repository.SharedPreferencesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import io.karn.notify.Notify
import io.karn.notify.internal.utils.Action

class FCMService : FirebaseMessagingService() {

    private val bRepo = BackendRepository()
    private var auth = FirebaseAuth.getInstance()
    private val preference = SharedPreferencesRepository(this)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        logd("onMessageReceived")
        if (message.data.isNotEmpty()) {
//            logd("Message data payload: ${message.data}")
            if (message.data["notifType"] == Constant.SWAMB_AUTH_TYPE) {
                val authData = Gson().fromJson(message.data.toString(), AuthenticationData::class.java)

//                val intent = Intent(this, BiometricAuthenticationActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                intent.putExtra(Constant.INTENT_PARAM_AUTH_DATA, authData)
//                startActivity(intent)

                var lastNotificationID = preference.lastNotificationID

                if (lastNotificationID == null) {
                    lastNotificationID = Constant.DEFAULT_NOTIFICATION_ID
                } else {
                    lastNotificationID++
                }

                sendAuthenticationNotification(lastNotificationID, authData)

            }
        }

        message.notification?.let {
            logd("Message Notification Body: ${it.body}")
        }

    }

    private fun sendAuthenticationNotification(notificationID: Int, authData: AuthenticationData) {
        preference.lastNotificationID = notificationID

        Notify.defaultConfig {
            alerting(this@FCMService.getString(R.string.authentication_notification_channel_id)) {
                channelName = this@FCMService.getString(R.string.authentication_notification_channel_name)
                channelDescription = this@FCMService.getString(R.string.authentication_notification_channel_description)
            }
        }

        logd("FCMService.authData: ${Gson().toJson(authData)}")

        Notify.with(this)
            .content {
                title = "SWAMB Authentication request from ${authData.vendorName}"
                text = "Authenticate account: ${authData.vendorUserID} on ${authData.vendorName}"
            }
//            .asBigText {
//                title = "SWAMB Authentication request from ${authData.vendorName}"
//                text = "Authenticate account: ${authData.vendorUserID} on ${authData.vendorName}"
//                expandedText = "Authenticate account: ${authData.vendorUserID} on ${authData.vendorName}"
//                bigText = ""
//            }
            .meta {
                cancelOnClick = true
                val openMainActivityToAuthIntent = Intent(this@FCMService, MainActivity::class.java).apply {
                    action = Constant.INTENT_ACTION_AUTH
                    putExtra(Constant.INTENT_PARAM_AUTH_DATA, authData)
                }

                val openMainActivityToAuthPendingIntent = PendingIntent.getActivity(this@FCMService, 1337, openMainActivityToAuthIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                clickIntent = openMainActivityToAuthPendingIntent
//                clearIntent = cancel/deny auth

            }
            .actions {
                add(
                    Action(
                        R.drawable.ic_app_icon,
                        "ACTION1",
                        null
                    )
                )
            }
            .show(notificationID)
    }

    override fun onNewToken(refreshedToken: String) {
        super.onNewToken(refreshedToken)
        logd("onNewToken: $refreshedToken")
        logd("token length: ${refreshedToken.length}")

        val preferences = SharedPreferencesRepository(this)
        preferences.fcmToken = refreshedToken
        if (refreshedToken.isNotEmpty()) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val updateFCMTokenRequest = UpdateFCMTokenRequest(currentUser.uid, refreshedToken)
                bRepo.updateFCMToken(updateFCMTokenRequest).responseObject(BackendResponse.Deserializer()) { req, res, updateFCMTokenResult ->
                    updateFCMTokenResult.fold(success = { data ->
                        logd(Gson().toJson(data))
                        if (data.success) {
                            logd("Update FCM Token Success")
                        } else {
                            loge("Fail to update FCM Token")
                        }
                    }, failure = { error ->
                        loge("Fail to update FCM Token")
                        loge(error.toString())
                    })

                }
            }
        } else {
            logd("token is empty")
        }
    }


}
