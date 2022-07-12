package com.lge.support.second.application.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.TooltipCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentExhibitsBinding
import com.lge.support.second.application.databinding.FragmentExhibitsUngjinBinding
import com.lge.support.second.application.model.MainViewModel
import com.lge.support.second.application.view.adapter.FragmentAdapter
import com.lge.support.second.application.view.adapter.answerList2Adapter
import com.lge.support.second.application.view.adapter.answerlist2Model
import com.lge.support.second.application.view.tabView.*
import kotlinx.coroutines.*
import org.apache.log4j.chainsaw.Main
import java.util.*
import kotlin.collections.ArrayList


class exhibits : Fragment() {

    private lateinit var binding: FragmentExhibitsBinding

    //tab에 연결해 줄 view들 (tab view - list)
    var viewList = ArrayList<View>()
    val viewModel by activityViewModels<MainViewModel>()

    //fragment_test1에서 해야 할 작업
    //이미지 부분
    lateinit var imageSwitcher: ImageSwitcher
    lateinit var arrImage: IntArray

    //공통 사용
    lateinit var btnNext: Button
    lateinit var btnBack: Button

    //tab menu text
    lateinit var tab1: String
    lateinit var tab2: String

    //var position : Int = 0

    var Arrtype = ArrayList<String>()
    var isCreatedThisPage = false
    var adapter: answerList2Adapter? = null

    companion object {
        var nameList = ArrayList<answerlist2Model>()
    }

    var isInit: Boolean = true;

    init {
        isInit = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).findViewById<ConstraintLayout>(R.id.background)
            .setBackgroundResource(R.drawable.gongju_background_2)

        binding = FragmentExhibitsBinding.inflate(inflater, container, false)

        Log.d("exhibits", "onCreateView")

        if (!isInit) {
            Log.d("exhibits", "called in exhibits: 전시된 작품 뭐 있어 $isInit")
            lifecycleScope.launch(Dispatchers.Default) {
                //viewModel.breakChat()
                viewModel.getResponse("전시된 작품 뭐 있어", changePage = false)
                viewModel.ttsStop()
            }
        } else {
            isInit = false
        }
        //보여질 화면 세팅
        MainActivity.viewModel.updatePageInfo("exhibits-ungjin")

        //if(!isCreatedThisPage){
        viewList.clear()
        viewList.add(layoutInflater.inflate(R.layout.fragment_exhibits_ungjin, null))
        viewList.add(layoutInflater.inflate(R.layout.fragment_exhibits_treasure, null))
        //}

        binding.exhibitsViewPager.adapter = pagerAdapter()

        //tab - view connect
        binding.exhibitsTab.setupWithViewPager(binding.exhibitsViewPager)

        tab1 = resources.getString(R.string.exhibits_ungjin_b1)
        tab2 = resources.getString(R.string.exhibits_ungjin_b2)
        //tab
        binding.exhibitsTab.getTabAt(0)?.text = tab1
        binding.exhibitsTab.getTabAt(1)?.text = tab2

        for (i in 0 until binding.exhibitsTab.tabCount) {
            Objects.requireNonNull(binding.exhibitsTab.getTabAt(i))
                ?.let { TooltipCompat.setTooltipText(it.view, null) }
        }

        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }

            if (!isCreatedThisPage) {
                for (question in it.messages[0].imageButton) {
                    nameList.add(answerlist2Model(question.text, question.url))
                    Arrtype.add(question.type)
                }
                isCreatedThisPage = true
                adapter!!.notifyDataSetChanged()
            }
        }

        adapter = answerList2Adapter(nameList, activity?.getApplicationContext())
        viewList[0].findViewById<GridView>(R.id.ungjin_list).adapter = adapter

        viewList[0].findViewById<GridView>(R.id.ungjin_list)
            .setOnItemClickListener { adapterView, view, position, id ->
                Toast.makeText(context, "click $id", Toast.LENGTH_SHORT).show()
                Log.i("exhibits", "text is " + nameList[position].text.toString())
                println("$Arrtype")
                Log.i("exhibits", "in_type is " + Arrtype[position])

                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.resetCurrentPage()
                    MainActivity.viewModel.getResponse(
                        nameList[position].text.toString(),
                        Arrtype[position]
                    )
                }
//                (activity as MainActivity).changeFragment("answer_3")
            }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d("exhibits", "onResume")
    }

    override fun onPause() {
        super.onPause()
        viewModel.ttsStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("exhibits", "onDestroy")
    }


    inner class pagerAdapter : PagerAdapter() {
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