package com.fhanjacson.swamb_client_android.model

data class RegisterUserRequest (
    val userID: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = ""
)