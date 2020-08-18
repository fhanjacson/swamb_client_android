package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class AuthenticationHistoryResponse (
    var success: Boolean = false,
    var message: String = "",
    var results: ArrayList<AuthenticationHistory> = ArrayList()
) {
    class Deserializer : ResponseDeserializable<AuthenticationHistoryResponse> {
        override fun deserialize(content: String): AuthenticationHistoryResponse = Gson().fromJson(content, AuthenticationHistoryResponse::class.java)
    }

    data class AuthenticationHistory (
        var authID: Int = -1,
        var linkageID: Int = -1,
        var authMessage: String = "",
        var authTimestamp: Long = 0
    )
}



