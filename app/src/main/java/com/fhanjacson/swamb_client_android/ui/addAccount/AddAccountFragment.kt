package com.fhanjacson.swamb_client_android.ui.addAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentAddAccountBinding


class AddAccountFragment : BaseFragment() {

    private var _binding: FragmentAddAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAccountBinding.inflate(inflater, container, false)
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