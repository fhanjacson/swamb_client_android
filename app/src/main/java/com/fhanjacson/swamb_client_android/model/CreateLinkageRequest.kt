package com.fhanjacson.swamb_client_android.model

data class CreateLinkageRequest (
    var token: String,
    var userID: String,
    var deviceID: Int
)