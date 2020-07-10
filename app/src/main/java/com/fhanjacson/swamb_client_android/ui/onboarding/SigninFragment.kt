package com.fhanjacson.swamb_client_android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.Constant.Companion.loge
import com.fhanjacson.swamb_client_android.MainActivity
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentSigninBinding
import com.fhanjacson.swamb_client_android.model.BackendResponse
import com.fhanjacson.swamb_client_android.model.CanLoginResponse
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SigninFragment : BaseFragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()
    private var bRepo = BackendRepository()


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
            binding.buttonSignin.isEnabled = false
            auth.signInWithEmailAndPassword("fhan.jacson@gmail.com", "password").addOnSuccessListener {
                val currentUser = it.user
                if (currentUser != null) {
                    canLogin(currentUser)
                }
            }.addOnFailureListener {
                toast("Fail to Sign in")
                loge(it.toString())
                binding.buttonSignin.isEnabled = true
            }
        }
    }

    private fun canLogin(currentUser: FirebaseUser) {
        bRepo.canLogin(currentUser.uid).responseObject(CanLoginResponse.Deserializer()) { req, res, canLoginResult ->
            canLoginResult.fold(success = { data ->
                if (data.status == 200) {
                    if (data.data.canLogin) {
                        proceedToMainActivity()
                    } else {
                        MaterialDialog(requireActivity()).show {
                            title(text = "Cant Login on this Device")
                            message(text = "You've setup another Device for SWAMB Authentication, therefore you cant login on this device. Either use the previous Device or Invalidate all your device and Setup this Device for SWAMB Authentication")
                            positiveButton(text = "Cancel Login") {
                                auth.signOut()
                            }
                            negativeButton(text = "Invalidate all Device") {
                                invalidateAllDevice(currentUser.uid)
                            }
                            cancelable(false)
                            cancelOnTouchOutside(false)
                        }
                        binding.buttonSignin.isEnabled = true
                    }

                }
            }, failure = { error ->
                toast("Fail to check can Login")
                loge("Fail to check can Login")
                loge(error.toString())
            })
        }
    }

    private fun proceedToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun invalidateAllDevice(userID: String) {
        bRepo.invalidateAllDevice(userID).responseObject(BackendResponse.Deserializer()) { req, res, invalidateAllDeviceResult ->
            invalidateAllDeviceResult.fold(success = { data ->
                if (data.status == 200) {
                    proceedToMainActivity()
                }
            }, failure = { error ->
                toast("Fail to Invalidate All Device")
                loge("Fail to Invalidate All Device")
                loge(error.toString())
                auth.signOut()
            })
        }
    }

}