package com.fhanjacson.swamb_client_android.repository

import com.fhanjacson.swamb_client_android.model.FCMToken
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreRepository {

    private var db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userCollection = db.collection("users")

    private fun getUserID(): String {
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.uid.isNotEmpty()) {
            return currentUser.uid
        } else {
            return "null"
        }
    }

    fun saveFCMToken(fcmToken: FCMToken): Task<Void> {
        return userCollection.document(getUserID()).collection("fcm_tokens").document(fcmToken.token).set(fcmToken, SetOptions.merge())
    }
}