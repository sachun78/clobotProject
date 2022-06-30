package com.lge.support.second.application.view

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatFailBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        val adapter = FragmentAdapter(this)
        val fragments = listOf<Fragment>(chat_faq(), chat_click(), chat_knowledge())
        val tabTitles = listOf<String>("FAQ", "용어사전", "극장상식")
        adapter.fragments = fragments

        binding.chatViewPager.adapter = adapter
        binding.chatViewPager.setUserInputEnabled(false);

        TabLayoutMediator(binding.chatTab, binding.chatViewPager) {
                tab, position -> tab.text = tabTitles[position]
        }.attach()

        return binding.root
    }

}
