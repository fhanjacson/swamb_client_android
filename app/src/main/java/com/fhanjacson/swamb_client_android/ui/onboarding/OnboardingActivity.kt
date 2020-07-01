package com.fhanjacson.swamb_client_android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.MainActivity
import com.fhanjacson.swamb_client_android.base.BaseActivity
import com.fhanjacson.swamb_client_android.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import java.util.HashMap


class OnboardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupData()
        setupUI()
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser?.uid != null) {
            proceedToMainActivity()
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


    private inner class OnboardingViewPagerAdapter(context: FragmentActivity, var viewpagerItems: ArrayList<Pair<String, Fragment>>): FragmentStateAdapter(context) {
        override fun getItemCount(): Int {
            return viewpagerItems.size
        }

        override fun createFragment(position: Int): Fragment {
            return viewpagerItems[position].second
        }
    }

}

