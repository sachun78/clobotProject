package com.lge.support.second.application.view.tabView

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentChatFaqBinding
import com.lge.support.second.application.view.adapter.faqCustomAdapter
import com.lge.support.second.application.view.adapter.questionModel
import com.lge.support.second.application.view.chatView.chat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class chat_faq : Fragment() {
    private var _binding: FragmentChatFaqBinding? = null
    private val binding get() = _binding!!

    var click: Boolean = false

    var modelList = ArrayList<questionModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatFaqBinding.inflate(inflater, container, false)

        var customAdapter =
            getActivity()?.getApplicationContext()?.let { faqCustomAdapter(modelList, it) }

        binding.faqGridView.adapter = customAdapter

        if (click == false) {
            for (i in chat.questions.indices) {
                modelList.add(questionModel(chat.questions[i]))
            }
            click = true
        }

        binding.faqGridView.setOnItemClickListener { adapterView, view, i, l ->
            GlobalScope.launch {
                MainActivity.viewModel.getResponse(chat.questions[i])
            }
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
        Log.d("tk_test", "faq destroy")
    }
}