package com.fhanjacson.swamb_client_android.model

import java.util.*

data class FCMToken(
    var token: String = "",
    var tokenDate: Date? = Date(),
    var platform: String = "android",
    var deviceName: String = "Generic Android Device",
    var deviceNickname: String = ""
)