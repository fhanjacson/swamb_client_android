package com.fhanjacson.swamb_client_android.model

data class ValidateAuthenticationRequest (
    var signedData: String,
    var linkageID: Int,
    var authLogID: Int
)