package com.fhanjacson.swamb_client_android.ui.linkage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LinkageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Linkage Fragment"
    }
    val text: LiveData<String> = _text
}