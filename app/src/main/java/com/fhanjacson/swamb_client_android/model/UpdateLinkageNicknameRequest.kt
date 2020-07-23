package com.fhanjacson.swamb_client_android.model

data class UpdateLinkageNicknameRequest (
    val userID: String,
    val linkageID: Int,
    val nickname: String
)