package com.lge.support.second.application.view

import android.R.id.tabs
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentLocationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class location : Fragment() {

    private lateinit var binding : FragmentLocationBinding

    var viewList = ArrayList<View>()

    //tab menu text
    lateinit var tab1 : String
    lateinit var tab2 : String

    //선택된 버튼
    lateinit var selectedBtn : Button
    lateinit var currentPin : View

//    var isInit: Boolean = true;
//
//    init {
//        isInit = true
//    }

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

        for (i in 0 until binding.exhibitsTab.tabCount) {
            Objects.requireNonNull(binding.exhibitsTab.getTabAt(i))
                ?.let { TooltipCompat.setTooltipText(it.view, null) }
        }

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

        val pin1 = viewList[0].findViewById<ImageView>(R.id.pin_1) //웅진백제실
        val pin2 = viewList[0].findViewById<ConstraintLayout>(R.id.pin_2) //화장실
        val pin3 = viewList[0].findViewById<ImageView>(R.id.pin_3) //기획전시실
        val pin4 = viewList[0].findViewById<ImageView>(R.id.pin_4) //세미나실
        val pin5 = viewList[0].findViewById<ImageView>(R.id.pin_5) //충청권역 수장고
        val pin6 = viewList[0].findViewById<ImageView>(R.id.pin_6) //디지털 실감영상관
        val pin7 = viewList[0].findViewById<ImageView>(R.id.pin_7) //수유실
        val pin8 = viewList[0].findViewById<ImageView>(R.id.pin_8) //웅진백제어린이체험실
        val pin9 = viewList[0].findViewById<ImageView>(R.id.pin_9) //강당

        //기본 화면
        selectedBtn = menuBtn1
        selectedBtn.isSelected = true
        currentPin = pin1
        currentPin.visibility = View.VISIBLE

        //클릭된 상태->action
        menuBtn1.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn1
            currentPin = pin1
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        menuBtn2.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn2
            currentPin = pin2
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        menuBtn3.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn3
            currentPin = pin3
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        menuBtn4.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn4
            currentPin = pin4
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        menuBtn5.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn5
            currentPin = pin5
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        menuBtn6.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn6
            currentPin = pin6
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        menuBtn7.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn7
            currentPin = pin7
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        menuBtn8.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn8
            currentPin = pin8
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        menuBtn9.setOnClickListener {
            selectedBtn.isSelected = false
            currentPin.visibility = View.INVISIBLE

            selectedBtn = menuBtn9
            currentPin = pin9
            selectedBtn.isSelected = true
            currentPin.visibility = View.VISIBLE
        }

        //...................viewList[1]........................//
        val btn1 = viewList[1].findViewById<Button>(R.id.btn1)
        val btn2 = viewList[1].findViewById<Button>(R.id.btn2)
        val btn3 = viewList[1].findViewById<Button>(R.id.btn3)
        val btn4 = viewList[1].findViewById<Button>(R.id.btn4)
        val btn5 = viewList[1].findViewById<Button>(R.id.btn5)
        val btn6 = viewList[1].findViewById<Button>(R.id.btn6)

        btn1.setOnClickListener {
            (activity as MainActivity).changeFragment("answer-exhibits")
//            lifecycleScope.launch(Dispatchers.Default) {
//                if (!isInit) {
//                    Log.d("exhibits", "called in exhibits: 전시된 작품 뭐 있어 $isInit")
//                    lifecycleScope.launch(Dispatchers.Default) {
////                        MainActivity.viewModel.breakChat()
////                        MainActivity.viewModel.getResponse("전시된 작품 뭐 있어")
////                        MainActivity.viewModel.ttsStop()
////                        MainActivity.viewModel.getResponse("전시된 작품 뭐 있어")
////                        MainActivity.viewModel.ttsStop()
////                        MainActivity.viewModel.resetCurrentPage()
////                        MainActivity.viewModel.getResponse("유동", "param")
//                    }
//                } else {
//                    isInit = false
//                }
//            }
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