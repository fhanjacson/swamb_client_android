package com.fhanjacson.swamb_client_android.ui.linkage

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.Glide
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentLinkageDetailBinding
import com.fhanjacson.swamb_client_android.model.BackendResponse
import com.fhanjacson.swamb_client_android.model.DeleteLinkageRequest
import com.fhanjacson.swamb_client_android.model.LinkageData
import com.fhanjacson.swamb_client_android.model.UpdateLinkageNicknameRequest
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LinkageDetailFragment : BaseFragment() {

    private var _binding: FragmentLinkageDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<LinkageDetailFragmentArgs>()

    private val auth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private val bRepo = BackendRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLinkageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser != null) {
            currentUser = auth.currentUser!!
        }

        val linkageData = args.linkageData
        setupUI(linkageData)
    }

    fun setupUI(linkageData: LinkageData) {
        binding.vendorName.text = linkageData.vendorName
        binding.vendorUserID.text = linkageData.vendorUserID
        Glide.with(this).load(linkageData.vendorIconUrl).into(binding.imageView)

        binding.buttonEditLinkageNickname.setOnClickListener {
            val inputType = InputType.TYPE_CLASS_TEXT
            MaterialDialog(requireActivity()).show {
                title(text = "Update Nickname")
                message(text = "Enter new nickname")
                input(
                    allowEmpty = false,
                    hint = "Linkage Nickname",
                    prefill = linkageData.linkageNickname,
                    inputType = inputType
                ) { dialog, text ->
                    if (text.toString() != linkageData.linkageNickname) {
                        updateNickname(currentUser.uid, linkageData.linkageID, text.toString())
                    }
                }
                negativeButton()
            }
        }

        binding.buttonDeleteLinkage.setOnClickListener {
            MaterialDialog(requireActivity()).show {
                title(text = "Delete Confirmation")
                message(text = "You wont be able to authenticate using this phone after you delete (un-link) the linkage")
                positiveButton {
                    deleteLinkage(currentUser.uid, linkageData.linkageID)
                }
                negativeButton()
            }
        }

    }

    private fun updateNickname(userID: String, linkageID: Int, nickname: String) {
        if (nickname.length < 64) {
            val updateLinkageNicknameRequest = UpdateLinkageNicknameRequest(userID, linkageID, nickname)
            bRepo.updateLinkageNickname(updateLinkageNicknameRequest).responseObject(BackendResponse.Deserializer()) { req, res, updateLinkageNicknameResult ->
                updateLinkageNicknameResult.fold(success = {data ->
                    if (data.success) {
                        toast("Update Linkage Nickname success")
                    }
                }, failure = { error ->
                    toast("Fail to Update Linkage Nickname")
                    Constant.loge("Fail to Update Linkage Nickname")
                    Constant.loge(error.toString())
                })
            }
        } else {
            toast("Nickname too long (Max: 64 characters)")
        }

    }

    private fun deleteLinkage(userID: String, linkageID: Int) {
        val deleteLinkageRequest = DeleteLinkageRequest(userID, linkageID)
        bRepo.deleteLinkage(deleteLinkageRequest).responseObject(BackendResponse.Deserializer()) { req, res, deleteLinkageResult ->
            deleteLinkageResult.fold(success = { data ->
                if (data.success) {
                    toast("Delete Linkage success")
                    findNavController().navigateUp()
                }
            }, failure = { error ->
                toast("Fail to Update Linkage Nickname")
                Constant.loge("Fail to Update Linkage Nickname")
                Constant.loge(error.toString())
            })

        }
    }


}