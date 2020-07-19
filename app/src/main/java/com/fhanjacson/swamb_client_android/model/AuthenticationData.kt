package com.fhanjacson.swamb_client_android.model

import java.io.Serializable

data class AuthenticationData (
    var randomString: String = "",
    var linkageID: String = "",
    var vendorName: String = "",
    var vendorUserID: String = ""
): Serializable