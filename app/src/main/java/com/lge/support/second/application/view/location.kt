package com.lge.support.second.application.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentLocationBinding


class location : Fragment() {

    private lateinit var binding : FragmentLocationBinding

    var viewList = ArrayList<View>()

    //tab menu text
    lateinit var tab1 : String
    lateinit var tab2 : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        mActivity.findViewById<ConstraintLayout>(R.id.background).setBackgroundResource(R.drawable.gongju_background_3)

        //보여질 화면 세팅
        viewList.add(layoutInflater.inflate(R.layout.location_facility, null))
        viewList.add(layoutInflater.inflate(R.layout.location_exhibits, null))

        binding.locationViewPager.adapter = pagerAdapter()

        //tab - view connect
        binding.exhibitsTab.setupWithViewPager(binding.locationViewPager)

        tab1 = "시설 위치"
        tab2 = "대표 전시품 위치"
        
        //tab
        binding.exhibitsTab.getTabAt(0)?.setText(tab1)
        binding.exhibitsTab.getTabAt(1)?.setText(tab2)

        Log.d("location", binding.exhibitsTab.getSelectedTabPosition().toString())



        return binding.root
    }

    inner class pagerAdapter : PagerAdapter(){
        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = viewList.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var curView = viewList[position]
            binding.locationViewPager.addView(curView)
            return curView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            binding.locationViewPager.removeView(`object` as View)
        }
    }

}