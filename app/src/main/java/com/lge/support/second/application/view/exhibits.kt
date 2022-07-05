package com.lge.support.second.application.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentExhibitsBinding
import com.lge.support.second.application.databinding.FragmentExhibitsUngjinBinding
import com.lge.support.second.application.view.adapter.FragmentAdapter
import com.lge.support.second.application.view.adapter.answerList2Adapter
import com.lge.support.second.application.view.adapter.answerlist2Model
import com.lge.support.second.application.view.tabView.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class exhibits : Fragment() {

    private lateinit var binding : FragmentExhibitsBinding

    //tab에 연결해 줄 view들 (tab view - list)
    var viewList = ArrayList<View>()

    //fragment_test1에서 해야 할 작업
    //이미지 부분
    lateinit var imageSwitcher: ImageSwitcher
    lateinit var arrImage : IntArray

    //공통 사용
    lateinit var btnNext : Button
    lateinit var btnBack : Button

    //tab menu text
    lateinit var tab1 : String
    lateinit var tab2 : String

    //var position : Int = 0

    var Arrtype = ArrayList<String>()
    var isCreatedThisPage = false
    var adapter: answerList2Adapter? = null
    var nameList = ArrayList<answerlist2Model>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExhibitsBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        //보여질 화면 세팅
        viewList.add(layoutInflater.inflate(R.layout.fragment_exhibits_ungjin, null))
        viewList.add(layoutInflater.inflate(R.layout.fragment_exhibits_treasure, null))

        binding.exhibitsViewPager.adapter = pagerAdapter()

        //tab - view connect
        binding.exhibitsTab.setupWithViewPager(binding.exhibitsViewPager)

        tab1 = "웅진 백제실"
        tab2 = "국보/보물"
        //tab
        binding.exhibitsTab.getTabAt(0)?.setText(tab1)
        binding.exhibitsTab.getTabAt(1)?.setText(tab2)

        GlobalScope.launch {
            MainActivity.viewModel.getResponse("전시된 작품 뭐 있어")
        }

        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {

            if (it == null) {
                return@observe
            }

            if (isCreatedThisPage == false) {
                for (question in it.messages[0].imageButton) {
                    nameList.add(answerlist2Model(question.text, question.url))
                    Arrtype.add(question.type)
                }
                isCreatedThisPage = true
            }
        }

        adapter = answerList2Adapter(nameList, getActivity()?.getApplicationContext())
        viewList[0].findViewById<GridView>(R.id.ungjin_list).adapter = adapter


        return binding.root
    }


    inner class pagerAdapter : PagerAdapter(){
        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = viewList.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var curView = viewList[position]
            binding.exhibitsViewPager.addView(curView)
            return curView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            binding.exhibitsViewPager.removeView(`object` as View)
        }
    }
}