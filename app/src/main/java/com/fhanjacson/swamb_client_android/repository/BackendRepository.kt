package com.fhanjacson.swamb_client_android.repository

import com.fhanjacson.swamb_client_android.model.InitDeviceRequest
import com.fhanjacson.swamb_client_android.model.UpdateFCMTokenRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson

class BackendRepository {

    private val baseURL = "https://6591b1b589f3.ngrok.io"

    fun initDevice(initDeviceRequest: InitDeviceRequest): Request {
        return Fuel.post("$baseURL/users/initDevice")
            .jsonBody(Gson().toJson(initDeviceRequest))

    }

    fun updateFCMToken(updateFCMTokenRequest: UpdateFCMTokenRequest): Request {
        return Fuel.post("$baseURL/users/updateFCMToken")
            .jsonBody(Gson().toJson(updateFCMTokenRequest))
    }
}