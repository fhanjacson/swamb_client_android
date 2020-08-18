package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class AuthenticationLogResponse(
    var success: Boolean = false,
    var message: String = "",
    var results: ArrayList<AuthenticationLog> = ArrayList()
) {
    class Deserializer : ResponseDeserializable<AuthenticationLogResponse> {
        override fun deserialize(content: String): AuthenticationLogResponse = Gson().fromJson(content, AuthenticationLogResponse::class.java)
    }

    data class AuthenticationLog(
        var linkageID: Int = -1,
        var ip: String = "",
        var browser: String = "",
        var version: String = "",
        var os: String = "",
        var platform: String = "",
        var authTimestamp: Long = 0,
        var vendorName: String = "",
        var vendorUserID: String = ""
    )
}