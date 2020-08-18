package com.fhanjacson.swamb_client_android.model

import java.io.Serializable

data class AuthenticationData (
    var randomString: String = "",
    var linkageID: String = "",
    var vendorName: String = "",
    var vendorUserID: String = "",
    var iat: String = "",
    var exp: String = "",
    var authLogID: String = "",
    var ip: String = "",
    var browser: String = "",
    var version: String = "",
    var platform: String = "",
    var os: String = ""
): Serializable {
//    data class AuthenticationMetaData(
//        var ip: String = "",
//        var browser: String = "",
//        var version: String = "",
//        var platform: String = "",
//        var os: String = ""
//    )
}