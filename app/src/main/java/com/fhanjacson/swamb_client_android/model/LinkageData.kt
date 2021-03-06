package com.fhanjacson.swamb_client_android.model

import java.io.Serializable

data class LinkageData (
    var linkageID: Int = -1,
    var linkageNickname: String = "",
    var vendorName: String = "",
    var vendorUserID: String = "",
    var vendorIconUrl: String = "",
    var linkageCreatedTimestamp: Long = 0,
    var lastAuthTimestamp: Long = 0
): Serializable