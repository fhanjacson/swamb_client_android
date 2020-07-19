package com.fhanjacson.swamb_client_android.repository

import android.content.Context

class SharedPreferencesRepository(contex: Context): SharedPreferencesHelper(contex) {
    var fcmToken by stringPref()
    var isKeyPairGenerated by booleanPref()
    var isDeviceInit by booleanPref()
    var deviceID by intPref()
    var lastNotificationID by intPref()

}