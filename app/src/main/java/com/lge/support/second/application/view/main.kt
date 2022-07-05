package com.lge.support.second.application.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.log4j.chainsaw.Main

class main : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        mActivity.findViewById<ConstraintLayout>(R.id.background).setBackgroundResource(R.drawable.gongju_background_1)
        mActivity.findViewById<Button>(R.id.backBtn).setBackgroundResource(R.drawable.back_main)
        mActivity.findViewById<Button>(R.id.homeBtn).setBackgroundResource(R.drawable.home_main)

        val menu1 = binding.mainMenu1
        val menu2 = binding.mainMenu2
        val menu3 = binding.mainMenu3
        val menu4 = binding.mainMenu4

        binding.gnbB1.setOnClickListener {
            mActivity.changeFragment("chat")
        }
//        mActivity.changeVisibility(1)
        mActivity.findViewById<LinearLayout>(R.id.top).visibility = View.VISIBLE

        menu1.setOnClickListener {
            mActivity.changeFragment("information")
        }

        menu2.setOnClickListener {
            mActivity.changeFragment("exhibits-ungjin")
        }

        menu3.setOnClickListener {
            mActivity.changeFragment("location-facility")
        }

        menu4.setOnClickListener {
//            val mqttInstance: MessageConnecter = MessageConnecter()
//            MainActivity.robotViewModel.docent1Request(mActivity)
//            mActivity.changeFragment("docent-end")
            mActivity.changeFragment("docent-select")
        }

        MainActivity.chatPage = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val mActivity = activity as MainActivity

        mActivity.findViewById<Button>(R.id.backBtn).setBackgroundResource(R.drawable.back)
        mActivity.findViewById<Button>(R.id.homeBtn).setBackgroundResource(R.drawable.home)
        MainActivity.viewModel.ttsStop()
        Log.d("tk_test", "main fragment view destoy")
    }
}