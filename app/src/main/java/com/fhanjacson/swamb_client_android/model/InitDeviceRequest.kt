package com.fhanjacson.swamb_client_android.model

import java.util.*

data class InitDeviceRequest (
    val userID: String = "",
    val fcmToken: String = "",
    val platform: String = "android",
    val deviceName: String = "Generic Android Device",
    val deviceNickname: String = "",
    val devicePublicKey: String = ""
)