package com.fhanjacson.swamb_client_android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhanjacson.swamb_client_android.Constant
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
import java.util.regex.Pattern

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
            signup()
        }
    }

    private fun signup() {
        if (!validateForm()) {
            binding.buttonSignup.isEnabled = true
            return
        }

        val email = binding.signupEmailText.text.toString()
        val password = binding.signupPasswordText.text.toString()
        val firstName = binding.signupFirstnameText.text.toString()
        val lastName = binding.signupLastnameText.text.toString()
        signup(email, password, firstName, lastName)
    }

    private fun signup(email: String, password: String, firstName: String, lastName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val currentUser = it.user
                if (currentUser != null) {
                    registerUser(currentUser, email, password, firstName, lastName)
                }
            }
            .addOnFailureListener {
                toast("Fail to Register User")
                loge("Fail to Register User")
                loge(it.toString())
                binding.buttonSignup.isEnabled = true
            }
    }

    private fun registerUser(currentUser: FirebaseUser, email: String, password: String, firstName: String, lastName: String) {
        val registerUserRequest = RegisterUserRequest(currentUser.uid, email, password, firstName, lastName)
        bRepo.registerUser(registerUserRequest).responseObject(BackendResponse.Deserializer()) { req, res, registerUserResult ->
            registerUserResult.fold(success = { data ->
                if (data.success) {
                    logd("Register User Success")
                    proceedToMainActivity()
                } else {
                    loge("Fail to Register User")
                }
            }, failure = { error ->
                toast("Fail to Register User")
                loge("Fail to Register User")
                loge(error.toString())
                binding.buttonSignup.isEnabled = true
            })
        }
    }

    private fun validateForm(): Boolean {
        var formValid = true
        val email = binding.signupEmailText.text.toString()
        val password = binding.signupPasswordText.text.toString()
        val firstName = binding.signupFirstnameText.text.toString()
        val lastName = binding.signupLastnameText.text.toString()

        if (firstName.isEmpty()) {
            formValid = false
            binding.signupFirstnameLayout.error = "First Name must not empty"
        } else if (!isNameValid(firstName)) {
            formValid = false
            binding.signupFirstnameLayout.error = "First Name is not valid (Max: 32 characters)"
        } else {
            binding.signupFirstnameLayout.error = null
        }

        if (lastName.isEmpty()) {
            formValid = false
            binding.signupLastnameLayout.error = "Last Name must not empty"
        } else if (!isNameValid(lastName)) {
            formValid = false
            binding.signupLastnameLayout.error = "Last Name is not valid (Max: 32 characters)"
        } else {
            binding.signupLastnameLayout.error = null
        }

        if (email.isEmpty()) {
            formValid = false
            binding.signupEmailLayout.error = "Email Address must not empty"
        } else if (email.length > 64) {
            formValid = false
            binding.signupEmailLayout.error = "Email must not exceed 64 characters"
        } else if (!isEmailValid(email)) {
            formValid = false
            binding.signupEmailLayout.error = "Email Address is not valid"
        } else {
            binding.signupEmailLayout.error = null
        }

        if (password.isEmpty()) {
            formValid = false
            binding.signupPasswordLayout.error = "Password must not empty"
        } else if (password.length > 64) {
            formValid = false
            binding.signupPasswordLayout.error = "Password must not exceed 64 characters"
        } else if (!isPasswordValid(password)) {
            formValid = false
            binding.signupPasswordLayout.error = "Password is not valid"
        } else {
            binding.signupPasswordLayout.error = null
        }

        return formValid
    }

    private fun isNameValid(name: String): Boolean {
        return name.length <= 32
    }

    private fun isEmailValid(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = Pattern.compile(Constant.REGEX_STRONG_PASSWORD)
        return passwordPattern.matcher(password).matches()
    }

    private fun proceedToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}