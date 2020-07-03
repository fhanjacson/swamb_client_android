package com.fhanjacson.swamb_client_android.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentHomeBinding
import com.fhanjacson.swamb_client_android.ui.authentication.BiometricAuthenticationActivity
import com.fhanjacson.swamb_client_android.ui.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        binding.buttonBiometricAuthentication.setOnClickListener {
            val intent = Intent(requireActivity(), BiometricAuthenticationActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSignout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), OnboardingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.buttonLiveBarcodeScanning.setOnClickListener {
//            val intent = Intent(requireActivity(), LiveBarcodeScanningActivity::class.java)
//            startActivity(intent)
            val action = HomeFragmentDirections.actionNavigationHomeToLiveBarcodeScanningFragment()
            findNavController().navigate(action)
        }
    }
}