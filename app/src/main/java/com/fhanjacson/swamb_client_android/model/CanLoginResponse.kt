package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class CanLoginResponse (
    val status: Int = -1,
    val message: String = "",
    val data: CanLogin = CanLogin()
) {
    class Deserializer : ResponseDeserializable<CanLoginResponse> {
        override fun deserialize(content: String): CanLoginResponse = Gson().fromJson(content, CanLoginResponse::class.java)
    }
}

data class CanLogin (
    val canLogin: Boolean = false
)