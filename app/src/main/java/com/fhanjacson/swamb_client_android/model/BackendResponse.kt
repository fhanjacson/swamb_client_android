package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class BackendResponse  (
    val status: Int = -1,
    val message: String = "",
    val success: Boolean = false
) {
    class Deserializer : ResponseDeserializable<BackendResponse> {
        override fun deserialize(content: String): BackendResponse = Gson().fromJson(content, BackendResponse::class.java)
    }
}