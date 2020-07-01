package com.fhanjacson.swamb_client_android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.MainActivity
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentSigninBinding
import com.google.firebase.auth.FirebaseAuth


class SigninFragment : BaseFragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
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
        binding.buttonSignin.setOnClickListener {
            auth.signInWithEmailAndPassword("fhan.jacson@gmail.com", "password").addOnSuccessListener {
                proceedToMainActivity()
            }.addOnFailureListener {
                toast("Fail to Sign in")
                loge(it.toString())
            }
        }
    }

    private fun proceedToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}