package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class InitDeviceResponse (
    val status: Int = -1,
    val message: String = "",
    val deviceID: Int = -1,
    val success: Boolean = false
) {
    class Deserializer : ResponseDeserializable<InitDeviceResponse> {
        override fun deserialize(content: String): InitDeviceResponse = Gson().fromJson(content, InitDeviceResponse::class.java)
    }
}