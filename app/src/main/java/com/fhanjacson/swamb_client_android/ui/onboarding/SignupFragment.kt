package com.fhanjacson.swamb_client_android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.MainActivity
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentSignupBinding
import com.fhanjacson.swamb_client_android.model.BackendResponse
import com.fhanjacson.swamb_client_android.model.RegisterUserRequest
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignupFragment : BaseFragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()
    private var bRepo = BackendRepository()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupUI()
    }

    private fun setupData() {

    }

    private fun setupUI() {
        binding.buttonSignup.setOnClickListener {
            binding.buttonSignup.isEnabled = false
            auth.createUserWithEmailAndPassword("fhan.jacson@gmail.com", "password")
                .addOnSuccessListener {
                    val currentUser = it.user
                    if (currentUser != null) {
                        registerUser(currentUser)
                    }
                }
                .addOnFailureListener {
                    toast("Fail to Register User")
                    loge("Fail to Register User")
                    loge(it.toString())
                    binding.buttonSignup.isEnabled = true
                }
        }
    }

    private fun registerUser(currentUser: FirebaseUser) {
        val registerUserRequest = RegisterUserRequest(userID = currentUser.uid, email = "fhan.jacson@gmail.com", firstName = "Fhan", lastName = "Jacson")
        bRepo.registerUser(registerUserRequest).responseObject(BackendResponse.Deserializer()) { req, res, registerUserResult ->
            registerUserResult.fold(success = { data ->
                if (data.status == 200) {
                    logd("Register User Success")
                    proceedToMainActivity()
                }
            }, failure = { error ->
                toast("Fail to Register User")
                loge("Fail to Register User")
                loge(error.toString())
                binding.buttonSignup.isEnabled = true
            })
        }
    }

    private fun proceedToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}