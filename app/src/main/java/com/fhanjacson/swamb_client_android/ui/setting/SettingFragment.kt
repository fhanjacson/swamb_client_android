package com.fhanjacson.swamb_client_android.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentSettingBinding
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.fhanjacson.swamb_client_android.repository.SharedPreferencesRepository
import com.fhanjacson.swamb_client_android.ui.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : BaseFragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()
    private lateinit var preference: SharedPreferencesRepository
    private var bRepo = BackendRepository()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        preference = SharedPreferencesRepository(requireActivity())
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

        val currentUser = auth.currentUser

        if (currentUser != null) {
            binding.settingEmailText.text = currentUser.email
        }



        binding.buttonSignout.setOnClickListener {
            MaterialDialog(requireActivity()).show {
                cancelable(false)
                cancelOnTouchOutside(false)
                title(text = "SIGN OUT WARNING")
                message(text = "SIGN OUT will remove all linkage associated to this account.\nYou can however, SIGN IN on another device and add new linkage on the new device")
                positiveButton {
                    auth.signOut()
                    preference.clearAll()
                    val intent = Intent(requireActivity(), OnboardingActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                negativeButton()
            }
        }

    }
}