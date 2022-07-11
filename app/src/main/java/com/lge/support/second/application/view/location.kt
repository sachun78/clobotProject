package com.lge.support.second.application.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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

    //선택된 버튼
    lateinit var selectedBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        mActivity.findViewById<ConstraintLayout>(R.id.background).setBackgroundResource(R.drawable.gongju_background_3)

        //보여질 화면 세팅
        viewList.clear()
        viewList.add(layoutInflater.inflate(R.layout.location_facility, null))
        viewList.add(layoutInflater.inflate(R.layout.location_exhibits, null))

        binding.locationViewPager.adapter = pagerAdapter()

        //tab - view connect
        binding.exhibitsTab.setupWithViewPager(binding.locationViewPager)

        tab1 = resources.getString(R.string.location_facility_b1)
        tab2 = resources.getString(R.string.location_facility_b2)
        
        //tab
        binding.exhibitsTab.getTabAt(0)?.setText(tab1)
        binding.exhibitsTab.getTabAt(1)?.setText(tab2)

        Log.d("location", binding.exhibitsTab.getSelectedTabPosition().toString())

        //...................viewList[0]........................//
        val menuBtn1 = viewList[0].findViewById<Button>(R.id.location_facility_b3)
        val menuBtn2 = viewList[0].findViewById<Button>(R.id.location_facility_b4)
        val menuBtn3 = viewList[0].findViewById<Button>(R.id.location_facility_b5)
        val menuBtn4 = viewList[0].findViewById<Button>(R.id.location_facility_b6)
        val menuBtn5 = viewList[0].findViewById<Button>(R.id.location_facility_b7)
        val menuBtn6 = viewList[0].findViewById<Button>(R.id.location_facility_b8)
        val menuBtn7 = viewList[0].findViewById<Button>(R.id.location_facility_b9)
        val menuBtn8 = viewList[0].findViewById<Button>(R.id.location_facility_b10)
        val menuBtn9 = viewList[0].findViewById<Button>(R.id.location_facility_b11)

        //기본 화면
        selectedBtn = menuBtn1
        selectedBtn.isSelected = true

        //클릭된 상태->action
        menuBtn1.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn1
            selectedBtn.isSelected = true
        }

        menuBtn2.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn2
            selectedBtn.isSelected = true
        }

        menuBtn3.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn3
            selectedBtn.isSelected = true
        }

        menuBtn4.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn4
            selectedBtn.isSelected = true
        }

        menuBtn5.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn5
            selectedBtn.isSelected = true
        }

        menuBtn6.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn6
            selectedBtn.isSelected = true
        }

        menuBtn7.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn7
            selectedBtn.isSelected = true
        }

        menuBtn8.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn8
            selectedBtn.isSelected = true
        }

        menuBtn9.setOnClickListener {
            Toast.makeText(context, "작업 필요", Toast.LENGTH_SHORT).show()
            selectedBtn.isSelected = false

            selectedBtn = menuBtn9
            selectedBtn.isSelected = true
        }

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