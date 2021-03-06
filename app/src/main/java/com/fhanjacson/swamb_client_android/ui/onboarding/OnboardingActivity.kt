package com.fhanjacson.swamb_client_android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.fhanjacson.swamb_client_android.MainActivity
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityOnboardingBinding
import com.fhanjacson.swamb_client_android.repository.SharedPreferencesRepository
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class OnboardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var auth = FirebaseAuth.getInstance()
    private lateinit var preference: SharedPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        preference = SharedPreferencesRepository(this)
        val view = binding.root
        setContentView(view)
        setupData()
        setupUI()
    }

    override fun onStart() {
        super.onStart()

        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (auth.currentUser?.uid != null) {
                    proceedToMainActivity()
                } else {
                    preference.clearAll()
                }
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                MaterialDialog(this).show {
                    title(text = "No Biometric features is supported on this device")
                    message(text = "Biometrics features is not supported by this device, you need a device that has biometrics capability")
                    positiveButton {
                        finish()
                    }
                    cancelable(false)
                    cancelOnTouchOutside(false)
                }
                toast("No biometric features available on this device.")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                MaterialDialog(this).show {
                    title(text = "Biometric features is unavailable")
                    message(text = "Biometrics features is currently unavailable, please try again later")
                    positiveButton {
                        finish()
                    }
                    cancelable(false)
                    cancelOnTouchOutside(false)
                }
                toast("Biometric features are currently unavailable.")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                MaterialDialog(this).show {
                    title(text = "No biometrics enrolled on this device")
                    message(text = "Please enroll a biometrics before continuing")
                    positiveButton {
                        finish()
                    }
                    cancelable(false)
                    cancelOnTouchOutside(false)
                }
                toast("The user hasn't associated any biometric credentials with their account.")
            }

        }
    }

    private fun setupData() {

    }

    private fun setupUI() {
        setupViewPager()

    }

    private fun setupViewPager() {
        val viewpagerItems = ArrayList<Pair<String, Fragment>>()
        viewpagerItems.add(Pair("Sign In", SigninFragment()))
        viewpagerItems.add(Pair("Sign Up", SignupFragment()))
        binding.onboardingViewpager.adapter = OnboardingViewPagerAdapter(this, viewpagerItems)
        TabLayoutMediator(binding.onboardingTablayout, binding.onboardingViewpager) { tab, position ->
            tab.text = viewpagerItems[position].first
        }.attach()
    }

    private fun proceedToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private inner class OnboardingViewPagerAdapter(context: FragmentActivity, var viewpagerItems: ArrayList<Pair<String, Fragment>>) :
        FragmentStateAdapter(context) {
        override fun getItemCount(): Int {
            return viewpagerItems.size
        }

        override fun createFragment(position: Int): Fragment {
            return viewpagerItems[position].second
        }
    }

}

