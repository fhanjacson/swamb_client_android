package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class CreateLinkageResponse (
    val status: Int = -1,
    val message: String = "",
    var linkageResult: Boolean = false
) {
    class Deserializer : ResponseDeserializable<CreateLinkageResponse> {
        override fun deserialize(content: String): CreateLinkageResponse = Gson().fromJson(content, CreateLinkageResponse::class.java)
    }
}