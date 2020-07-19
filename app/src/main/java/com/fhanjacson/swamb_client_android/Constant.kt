package com.fhanjacson.swamb_client_android

import android.util.Log

class Constant {
    companion object {

        const val DEFAULT_NOTIFICATION_ID: Int = 1337
        const val ANDROID_KEYSTORE: String = "AndroidKeyStore"
        const val DEVICE_KEYPAIR_ALIAS: String = "SWAMB_KEY_ALIAS"
        const val LOG_TAG = "PANTEQ"
        const val REQUEST_CODE_PERMISSIONS = 10
        const val SWAMB_AUTH_TYPE = "swamb_auth"
        const val INTENT_PARAM_AUTH_DATA = "authData"

        const val INTENT_ACTION_AUTH: String = "INTENT_ACTION_AUTH"



        fun logd(message: String) {
            Log.d(LOG_TAG, message)
        }

        fun loge(message: String) {
            Log.e(LOG_TAG, message)
        }

    }
}