package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentMainBinding
import com.lge.support.second.application.main.managers.mqtt.MessageConnecter

class main : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View ? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        val menu1 = binding.mainI1
        val menu2 = binding.mainI2
        val menu3 = binding.mainI3

//        mActivity.changeVisibility(1)
        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.VISIBLE

        menu1.setOnClickListener {
            mActivity.changeFragment("play")
        }

        menu2.setOnClickListener {
            mActivity.changeFragment("theater")
        }

        menu3.setOnClickListener {
            mActivity.changeFragment("enjoy")
        }

        binding.mainI4.setOnClickListener{
            val mqttInstance: MessageConnecter = MessageConnecter()
        }

        MainActivity.chatPage = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}