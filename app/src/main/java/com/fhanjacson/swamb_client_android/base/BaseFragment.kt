package com.fhanjacson.swamb_client_android.base

import android.widget.Toast
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    fun toast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    fun toastl(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }
}