package com.lge.support.second.application.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentTheaterParkingBusBinding
import com.lge.support.second.application.view.adapter.FragmentAdapter
import com.lge.support.second.application.view.tabView.theater_bus
import com.lge.support.second.application.view.tabView.theater_parking

class theater_parking_bus : Fragment() {

    private var _binding : FragmentTheaterParkingBusBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTheaterParkingBusBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        val adapter = FragmentAdapter(this)
        val fragments = listOf<Fragment>(theater_parking(), theater_bus())
        val tabTitles = listOf<String>("주차안내", "도보(노선버스)")
        adapter.fragments = fragments

        binding.theaterViewPager.adapter = adapter
        binding.theaterViewPager.setUserInputEnabled(false);

        TabLayoutMediator(binding.theaterTab, binding.theaterViewPager) {
                tab, position -> tab.text = tabTitles[position]
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}
