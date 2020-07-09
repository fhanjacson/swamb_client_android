package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class BackendErrorResponse(
    val errorMessage: String = "Error"
) {
    class Deserializer : ResponseDeserializable<BackendErrorResponse> {
        override fun deserialize(content: String): BackendErrorResponse = Gson().fromJson(content, BackendErrorResponse::class.java)
    }
}


