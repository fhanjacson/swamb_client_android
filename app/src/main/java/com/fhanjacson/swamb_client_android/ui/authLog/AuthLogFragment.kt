package com.fhanjacson.swamb_client_android.ui.authLog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fhanjacson.swamb_client_android.MainViewModel
import com.fhanjacson.swamb_client_android.databinding.FragmentAuthLogBinding
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.fhanjacson.swamb_client_android.ui.linkage.LinkageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthLogFragment : Fragment() {

    private var _binding: FragmentAuthLogBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private lateinit var viewModel: MainViewModel
    private val bRepo = BackendRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        _binding = FragmentAuthLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (auth.currentUser != null) {
            currentUser = auth.currentUser!!
        }
        setupAuthLog()
        setupUI()
    }

    private fun setupAuthLog() {
        viewModel.authLogList.observe(viewLifecycleOwner, Observer { authLogList ->
            val viewManager = LinearLayoutManager(context)
            val viewAdapter = AuthLogAdapter(authLogList)

            binding.recyclerviewAuthLog.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        })
    }

    private fun setupUI() {}



}