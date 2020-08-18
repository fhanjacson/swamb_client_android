package com.fhanjacson.swamb_client_android.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class UserStatsResponse(
    val success: Boolean = false,
    val message: String = "",
    val userStats: UserStats = UserStats()
) {
    class Deserializer : ResponseDeserializable<UserStatsResponse> {
        override fun deserialize(content: String): UserStatsResponse = Gson().fromJson(content, UserStatsResponse::class.java)
    }

    data class UserStats(
        var totalAuth: Int = -1,
        var totalSuccessAuth: Int = -1
    )
}

