package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.main.view.tabView.theater_bus
import com.lge.support.second.application.main.view.tabView.theater_parking
import kr.co.clobot.robot.common.R
import kr.co.clobot.robot.common.databinding.FragmentTheaterParkingBusBinding

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
        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.GONE

        val adapter = FragmentAdapter(this)
        val fragments = listOf<Fragment>(theater_parking(), theater_bus())
        val tabTitles = listOf<String>("주차안내", "도보(노선버스)")
        adapter.fragments = fragments

        binding.theaterViewPager.adapter = adapter

        TabLayoutMediator(binding.theaterTab, binding.theaterViewPager) {
                tab, position -> tab.text = tabTitles[position]
        }.attach()

        return binding.root
    }

}

class FragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    var fragments = listOf<Fragment>()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}