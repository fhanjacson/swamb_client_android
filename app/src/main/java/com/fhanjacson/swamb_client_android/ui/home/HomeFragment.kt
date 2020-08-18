package com.fhanjacson.swamb_client_android.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fhanjacson.swamb_client_android.Constant
import com.fhanjacson.swamb_client_android.Constant.Companion.logd
import com.fhanjacson.swamb_client_android.MainViewModel
import com.fhanjacson.swamb_client_android.R
import com.fhanjacson.swamb_client_android.base.BaseFragment
import com.fhanjacson.swamb_client_android.databinding.FragmentHomeBinding
import com.fhanjacson.swamb_client_android.model.GetAllLinkageResponse
import com.fhanjacson.swamb_client_android.model.UserStatsResponse
import com.fhanjacson.swamb_client_android.repository.BackendRepository
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.skydoves.balloon.*


class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        preference = SharedPreferencesRepository(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (auth.currentUser != null) {
            currentUser = auth.currentUser!!
        }
        setupData()
        setupUI()
    }

    private fun setupData() {

    }

    private fun setupUI() {

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData(currentUser.uid)
        }

        binding.cardTotalAuth.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToAuthHistoryFragment()
            findNavController().navigate(action)
        }

        binding.helpButtonTotalLinkage.setOnClickListener {
            showTooltip(binding.helpButtonTotalLinkage, "Total Accounts Linked")
        }

        binding.helpButtonTotalAuth.setOnClickListener {
            showTooltip(binding.helpButtonTotalAuth, "Total Authentication request")
        }

        binding.helpButtonAuthRatio.setOnClickListener {
            showTooltip(binding.helpButtonAuthRatio, "Auth Success vs Deny Ratio")
        }

        binding.buttonChartTogglePercent.setOnClickListener {
            logd("clicked")
            binding.authChart.apply {
                setUsePercentValues(!binding.authChart.isUsePercentValuesEnabled)
                invalidate()
            }
        }

        viewModelObserving()
    }

    private fun viewModelObserving() {
        viewModel.userStats.observe(viewLifecycleOwner, Observer { userStats ->
            val entries: MutableList<PieEntry> = ArrayList()
            entries.add(PieEntry(userStats.totalSuccessAuth.toFloat(), "Success"))
            entries.add(PieEntry((userStats.totalAuth - userStats.totalSuccessAuth).toFloat(), "Fail"))

            val colors: MutableList<Int> = ArrayList()
            colors.add(Color.parseColor("#4cd137"))
            colors.add(Color.parseColor("#e84118"))

            val set = PieDataSet(entries, "")
            set.colors = colors
            set.valueTextSize = 16f

            val data = PieData(set)
            data.setValueFormatter(PercentFormatter(binding.authChart))

            binding.authChart.description.text = "Auth Success Ratio"
//        binding.authChart.holeRadius = 0f
            binding.authChart.setUsePercentValues(true)
            binding.authChart.isDrawHoleEnabled = false
            binding.authChart.setNoDataText("No data yet")
            binding.authChart.data = data
            binding.authChart.animateY(800, Easing.EaseInOutQuad)

            binding.totalAuthText.text = userStats.totalAuth.toString()

        })

        viewModel.linkageList.observe(viewLifecycleOwner, Observer { linkageList ->
            binding.totalLinkageText.text = linkageList.size.toString()
        })
    }

    private fun showTooltip(view: View, msg: String) {
        val balloon = createBalloon(requireActivity()) {
            setPadding(8)
            setArrowSize(10)
            setArrowOrientation(ArrowOrientation.LEFT)
            setCornerRadius(4f)
            setAlpha(0.9f)
            setText(msg)
            setTextColorResource(R.color.WHITE)
            setBackgroundColorResource(R.color.colorPrimary)
            setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            setLifecycleOwner(lifecycleOwner)
        }

        view.showAlignRight(balloon)
    }

    fun refreshData(userID: String) {
        bRepo.getAllLinkage(userID).responseObject(GetAllLinkageResponse.Deserializer()) { req, res, getAllLinkageResult ->
            getAllLinkageResult.fold(success = { data ->
                if (data.success) {
                    viewModel.linkageList.postValue(data.results)
                    getUserStats(userID)
                    logd("Success to Get all Linkage")
                } else {
                    Constant.loge("Fail to Get all Linkage")
                }
            }, failure = { error ->
                Constant.loge("Fail to Get all Linkage")
                Constant.loge(error.toString())
            })
        }
    }

    fun getUserStats(userID: String) {
        bRepo.getUserStats(userID).responseObject(UserStatsResponse.Deserializer()) { _, _, getUserStatsResult ->
            getUserStatsResult.fold(success = { data ->
                if (data.success) {
                    logd("Success to Get user stats")
                    viewModel.userStats.postValue(data.userStats)
                    binding.swipeRefreshLayout.isRefreshing = false
                } else {
                    Constant.loge("Fail to Get User Stats")
                }
            }, failure = { error ->
                Constant.loge("Fail to Get User Stats")
                Constant.loge(error.toString())
            })
        }
    }

}