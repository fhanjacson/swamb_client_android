package com.fhanjacson.swamb_client_android

import android.util.Log

class Constant {
    companion object {
        const val ANDROID_KEYSTORE: String = "AndroidKeyStore"
        const val DEVICE_KEYPAIR_ALIAS: String = "SWAMB_KEY_ALIAS"
        const val LOG_TAG = "PANTEQ"
        const val REQUEST_CODE_PERMISSIONS = 10


        fun logd(message: String) {
            Log.d(LOG_TAG, message)
        }

        fun loge(message: String) {
            Log.e(LOG_TAG, message)
        }

    }
}