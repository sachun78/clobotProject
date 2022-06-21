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
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentChatFailBinding
import com.lge.support.second.application.main.view.adapter.questionModel
import com.lge.support.second.application.main.view.tabView.*


class chat_fail : Fragment() {

    private var _binding: FragmentChatFailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatFailBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.GONE

        val adapter = chat_fragmentAdapter(this)
        val fragments = listOf<Fragment>(chat_faq(), chat_click(), chat_knowledge())
        val tabTitles = listOf<String>("FAQ", "용어사전", "극장상식")
        adapter.fragments = fragments

        binding.chatViewPager.adapter = adapter

        TabLayoutMediator(binding.chatTab, binding.chatViewPager) {
                tab, position -> tab.text = tabTitles[position]
        }.attach()


        return binding.root
    }

}

class chat_fragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    var fragments = listOf<Fragment>()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}