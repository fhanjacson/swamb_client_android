package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class GetAllLinkageResponse(
    val message: String = "",
    val success: Boolean = false,
    val results: ArrayList<LinkageData> = ArrayList()
) {
    class Deserializer : ResponseDeserializable<GetAllLinkageResponse> {
        override fun deserialize(content: String): GetAllLinkageResponse = Gson().fromJson(content, GetAllLinkageResponse::class.java)
    }
}