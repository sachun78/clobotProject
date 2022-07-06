package com.lge.support.second.application.view.chatView

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentChatBinding
import com.lge.support.second.application.view.adapter.questionModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class chat : Fragment() {

    private lateinit var binding: FragmentChatBinding


    ////////////"뜰아래극장이 어디에요?" "해오름극장이 어디에요?"고객지원센터는 어디에요? 시각장애인 국립극장은 무엇을 하는 곳인가요?
    var click: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<ConstraintLayout>(R.id.background)
            .setBackgroundResource(R.drawable.gongju_background_3)

        var dialog = Dialog(mActivity)
        dialog.setContentView(R.layout.count_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var dialogText: TextView = dialog.findViewById(R.id.dialog_testText)
        dialog.setCanceledOnTouchOutside(false)

        MainActivity.viewModel.speechStatus.observe(viewLifecycleOwner) {
            when (it) {
                "3" -> {
                    dialogText.setText("3")
                    dialog.show()
                }
                "2" -> {
                    dialogText.setText("2")
                    dialog.show()
                }
                "1" -> {
                    dialogText.setText("1")
                    dialog.show()
                }
                "END" -> {
                    dialog.hide()

                }
            }
        }

        binding.chatB1.text = "시각장애인"

        MainActivity.viewModel.speechText.observe(viewLifecycleOwner) {
            binding.chatX2.text = it
        }

        ///////chat page 진입 확인용//////////
        MainActivity.chatPage = true
        /////chat page에서 음성 입력 3회만 가능////
        MainActivity.notMachCnt = 0

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chatB1.setOnClickListener(ButtonListener())
//        binding.chatB1.setOnClickListener {
//            GlobalScope.launch {
//                MainActivity.viewModel.getResponse("시각 약자를 위한 작품해설이 있나요")
//            }
//            Log.d("chatPage", "click")
//        }

//        binding.chatB1.setOnClickListener {
//        MainActivity.viewModel.speechStop()
//            MainActivity.viewModel.speechResponse()
//        }

        binding.chatB11.setOnClickListener {
            (activity as MainActivity).changeFragment("chat-fail")
        }

        MainActivity.viewModel.speechResponse()
        MainActivity.viewModel.ischatfirst = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("tk_test", "chat page destroy")
        MainActivity.viewModel.ischatfirst = true
        MainActivity.viewModel.ttsStop()
        MainActivity.viewModel.speechStop()
    }

    inner class ButtonListener : View.OnClickListener {
        override fun onClick(v: View?) {
            GlobalScope.launch {
                when (v?.id) {
                    R.id.chat_b1 -> MainActivity.viewModel.getResponse(binding.chatB1.text.toString())
                }
            }
        }
    }
}