package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.clobot.robot.common.databinding.FragmentChatBinding

class chat : Fragment() {

    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_chat, container, false)
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

}