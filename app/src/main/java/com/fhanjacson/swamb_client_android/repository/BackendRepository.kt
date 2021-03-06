package com.fhanjacson.swamb_client_android.repository

import com.fhanjacson.swamb_client_android.model.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson

class BackendRepository {

    private val baseURL = "https://swamb-backend.herokuapp.com"

    fun registerUser(registerUserRequest: RegisterUserRequest): Request {
        return Fuel.post("$baseURL/users").jsonBody(Gson().toJson(registerUserRequest))
    }

    fun initDevice(initDeviceRequest: InitDeviceRequest): Request {
        return Fuel.post("$baseURL/users/initDevice").jsonBody(Gson().toJson(initDeviceRequest))
    }

    fun updateFCMToken(updateFCMTokenRequest: UpdateFCMTokenRequest): Request {
        return Fuel.post("$baseURL/users/updateFCMToken").jsonBody(Gson().toJson(updateFCMTokenRequest))
    }

    fun createLinkage(createLinkageRequest: CreateLinkageRequest): Request {
        return Fuel.post("$baseURL/swamb/linkage").jsonBody(Gson().toJson(createLinkageRequest))
    }

    fun canLogin(userID: String): Request {
        return Fuel.get("$baseURL/users/canLogin?userID=$userID")
    }

    fun invalidateAllDevice(userID: String): Request {
        return Fuel.post("$baseURL/users/invalidateAllDevice").jsonBody("{\"userID\": \"$userID\"}")
    }

    fun validateAuthentication(validateAuthenticationRequest: ValidateAuthenticationRequest): Request {
        return Fuel.post("$baseURL/swamb/validateAuthentication").jsonBody(Gson().toJson(validateAuthenticationRequest))
    }

    fun getAllLinkage(userID: String): Request {
        return Fuel.get("$baseURL/users/linkage?userID=$userID")
    }

    fun updateLinkageNickname(updateLinkageNicknameRequest: UpdateLinkageNicknameRequest): Request {
        return Fuel.put("$baseURL/users/linkage").jsonBody(Gson().toJson(updateLinkageNicknameRequest))
    }

    fun deleteLinkage(deleteLinkageRequest: DeleteLinkageRequest): Request {
        return Fuel.delete("$baseURL/users/linkage").jsonBody(Gson().toJson(deleteLinkageRequest))
    }

    fun getAuthenticationHistory(linkageID: Int): Request {
        return Fuel.get("$baseURL/users/authenticationHistory?linkageID=$linkageID")
    }

    fun getAuthenticationLog(userID: String): Request {
        return Fuel.get("$baseURL/users/authenticationLog?userID=$userID")
    }

    fun authFailLogger(linkageID: Int, authMessage: String): Request {
        return Fuel.post("$baseURL/swamb/authenticationFailLogger").jsonBody("{\"linkageID\": $linkageID, \"authMessage\": \"$authMessage\"}")
    }

    fun getUserStats(userID: String): Request {
        return Fuel.get("$baseURL/users/userStats?userID=$userID")
    }
}