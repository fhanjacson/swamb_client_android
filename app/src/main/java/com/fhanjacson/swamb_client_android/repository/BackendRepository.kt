package com.fhanjacson.swamb_client_android.repository

import com.fhanjacson.swamb_client_android.model.CreateLinkageRequest
import com.fhanjacson.swamb_client_android.model.InitDeviceRequest
import com.fhanjacson.swamb_client_android.model.RegisterUserRequest
import com.fhanjacson.swamb_client_android.model.UpdateFCMTokenRequest
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson

class BackendRepository {

//    private val baseURL = "https://swamb-backend.herokuapp.com"
    private val baseURL = "https://211478a4719e.ngrok.io"

    fun registerUser(registerUserRequest: RegisterUserRequest): Request {
        return Fuel.post("$baseURL/users")
            .jsonBody(Gson().toJson(registerUserRequest))
    }

    fun initDevice(initDeviceRequest: InitDeviceRequest): Request {
        return Fuel.post("$baseURL/users/initDevice")
            .jsonBody(Gson().toJson(initDeviceRequest))
    }

    fun updateFCMToken(updateFCMTokenRequest: UpdateFCMTokenRequest): Request {
        return Fuel.post("$baseURL/users/updateFCMToken")
            .jsonBody(Gson().toJson(updateFCMTokenRequest))
    }

    fun createLinkage(createLinkageRequest: CreateLinkageRequest): Request {
        return Fuel.post("$baseURL/swamb/linkage")
            .jsonBody(Gson().toJson(createLinkageRequest))
    }

    fun canLogin(userID: String): Request {
        return Fuel.get("$baseURL/users/canLogin?userID=$userID")
    }

    fun invalidateAllDevice(userID: String): Request {
        return Fuel.post("$baseURL/users/invalidateAllDevice")
            .jsonBody("{\"userID\": \"$userID\"}")
    }

}