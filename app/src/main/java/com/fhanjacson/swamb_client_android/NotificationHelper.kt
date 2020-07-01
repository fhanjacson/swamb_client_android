package com.fhanjacson.swamb_client_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult

class NotificationHelper(private val context: Context) {

    fun getFCMToken(): Task<InstanceIdResult> {
        return FirebaseInstanceId.getInstance().instanceId
    }

    fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                context.getString(R.string.authentication_notification_channel_id),
                context.getString(R.string.authentication_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(true)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description =
                    context.getString(R.string.authentication_notification_channel_description)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }
}