package com.lge.support.second.application.view.chatView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.tabs.TabLayoutMediator
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentChatFailBinding
import com.lge.support.second.application.view.adapter.FragmentAdapter
import com.lge.support.second.application.view.tabView.chat_click
import com.lge.support.second.application.view.tabView.chat_faq
import com.lge.support.second.application.view.tabView.chat_knowledge


class chat_fail : Fragment() {

    private lateinit var binding: FragmentChatFailBinding
    lateinit var tab1 : String
    lateinit var tab2 : String
    lateinit var tab3 : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatFailBinding.inflate(inflater, container, false)

//        val mActivity = activity as MainActivity
        tab1 = resources.getString(R.string.chat_faq_b1)
        tab2 = resources.getString(R.string.chat_faq_b2)
        tab3 = resources.getString(R.string.chat_faq_b3)

        val adapter = FragmentAdapter(this)
        val fragments = listOf<Fragment>(chat_faq(), chat_click(), chat_knowledge())
        val tabTitles = listOf<String>(tab1, tab2, tab3)

        adapter.fragments = fragments

        binding.chatViewPager.adapter = adapter
        binding.chatViewPager.setUserInputEnabled(false);

        TabLayoutMediator(binding.chatTab, binding.chatViewPager) {
                tab, position -> tab.text = tabTitles[position]
        }.attach()

        return binding.root
    }

}
