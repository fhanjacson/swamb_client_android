package com.fhanjacson.swamb_client_android.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentHomeBinding
import com.fhanjacson.swamb_client_android.repository.SharedPreferencesRepository
import com.fhanjacson.swamb_client_android.ui.authentication.BiometricAuthenticationActivity
import com.fhanjacson.swamb_client_android.ui.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
//    private var auth = FirebaseAuth.getInstance()
//    private lateinit var preference: SharedPreferencesRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        preference = SharedPreferencesRepository(requireActivity())
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


    }
}