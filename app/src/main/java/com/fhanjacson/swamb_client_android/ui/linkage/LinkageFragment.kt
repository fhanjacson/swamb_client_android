package com.fhanjacson.swamb_client_android.ui.linkage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.MainViewModel
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentLinkageBinding
import com.fhanjacson.swamb_client_android.model.GetAllLinkageResponse
import com.fhanjacson.swamb_client_android.model.LinkageData
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LinkageFragment : BaseFragment(), LinkageAdapter.LinkageClickListener {

    private lateinit var viewModel: MainViewModel
    private var _binding: FragmentLinkageBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private val bRepo = BackendRepository()
    private var linkageList = ArrayList<LinkageData>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        _binding = FragmentLinkageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (auth.currentUser != null) {
//            currentUser = auth.currentUser!!
//            setupLinkage(currentUser)
//        }

        binding.fabAddLinkage.setOnClickListener {
            val action = LinkageFragmentDirections.actionNavigationLinkageToLiveBarcodeScanningFragment()
            findNavController().navigate(action)
        }

        setupLinkageRecyclerview()

    }

//    private fun setupLinkage(currentUser: FirebaseUser) {
//        bRepo.getAllLinkage(currentUser.uid).responseObject(GetAllLinkageResponse.Deserializer()) { req, res, getAllLinkageResult ->
//            getAllLinkageResult.fold(success = { data ->
//                if (data.success) {
//                    linkageList = data.results
//                    setupLinkageRecyclerview()
//                    Constant.logd("Success to Get all Linkage")
//                } else {
//                    Constant.loge("Fail to Get all Linkage")
//                }
//            }, failure = { error ->
//                toast("Fail to Get all Linkage")
//                Constant.loge("Fail to Get all Linkage")
//                Constant.loge(error.toString())
//            })
//        }
//    }

    private fun setupLinkageRecyclerview() {
        viewModel.linkageList.observe(viewLifecycleOwner, Observer { linkageList ->
            val viewManager = LinearLayoutManager(context)
            val viewAdapter = LinkageAdapter(linkageList, this, this)

            binding.recyclerviewLinkage.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
//            binding.recyclerviewLinkage.adapter?.notifyDataSetChanged()
        })
    }

    override fun onItemViewClick(linkage: LinkageData) {
        val action = LinkageFragmentDirections.actionNavigationLinkageToLinkageDetailFragment(linkage)
        findNavController().navigate(action)
    }


}