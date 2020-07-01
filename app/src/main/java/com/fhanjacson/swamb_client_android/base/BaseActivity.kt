package com.fhanjacson.swamb_client_android.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {
    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun toastl(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}