package com.fhanjacson.swamb_client_android.ui.qr_scanner

import android.os.Bundle
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityLiveBarcodeScanningBinding


class LiveBarcodeScanningActivity : BaseActivity() {

    private lateinit var binding: ActivityLiveBarcodeScanningBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveBarcodeScanningBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


}