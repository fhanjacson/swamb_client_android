package com.fhanjacson.swamb_client_android.base

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity: AppCompatActivity() {
    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun toastl(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    fun hasPermissions(requiredPermissions: Array<String>) = requiredPermissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

}