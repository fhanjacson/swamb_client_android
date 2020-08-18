package com.fhanjacson.swamb_client_android

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fhanjacson.swamb_client_android.model.AuthenticationLogResponse
import com.fhanjacson.swamb_client_android.model.GetAllLinkageResponse
import com.fhanjacson.swamb_client_android.model.LinkageData
import com.fhanjacson.swamb_client_android.model.UserStatsResponse
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {

    private val bRepo = BackendRepository()
    private val auth = FirebaseAuth.getInstance()

    val userStats: MutableLiveData<UserStatsResponse.UserStats> by lazy {
        MutableLiveData<UserStatsResponse.UserStats>().also {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                getUserStats(currentUser.uid)
            }
        }
    }

    val linkageList: MutableLiveData<ArrayList<LinkageData>> by lazy {
        MutableLiveData<ArrayList<LinkageData>>().also {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                getLinkageList(currentUser.uid)
            }
        }
    }

    val authLogList: MutableLiveData<ArrayList<AuthenticationLogResponse.AuthenticationLog>> by lazy {
        MutableLiveData<ArrayList<AuthenticationLogResponse.AuthenticationLog>>().also {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                getAuthLog(currentUser.uid)
            }
        }
    }

    fun getLinkageList(userID: String) {
        bRepo.getAllLinkage(userID).responseObject(GetAllLinkageResponse.Deserializer()) { req, res, getAllLinkageResult ->
            getAllLinkageResult.fold(success = { data ->
                if (data.success) {
                    linkageList.postValue(data.results)
                    Constant.logd("Success to Get all Linkage")
                } else {
                    Constant.loge("Fail to Get all Linkage")
                }
            }, failure = { error ->
                Constant.loge("Fail to Get all Linkage")
                Constant.loge(error.toString())
            })
        }
    }

    fun getUserStats(userID: String) {
        bRepo.getUserStats(userID).responseObject(UserStatsResponse.Deserializer()) { _, _, getUserStatsResult ->
            getUserStatsResult.fold(success = { data ->
                if (data.success) {
                    Constant.logd("Success to Get user stats")
                    userStats.postValue(data.userStats)
                } else {
                    Constant.loge("Fail to Get User Stats")
                }
            }, failure = { error ->
                Constant.loge("Fail to Get User Stats")
                Constant.loge(error.toString())
            })
        }
    }

    fun getAuthLog(userID: String) {
        bRepo.getAuthenticationLog(userID).responseObject(AuthenticationLogResponse.Deserializer()) { _, _, getAuthLogResult ->
            getAuthLogResult.fold(success = {data ->
                authLogList.postValue(data.results)
            }, failure = {error ->
                Constant.loge("Fail to Get Auth Log")
                Constant.loge(error.toString())
            })

        }
    }

}