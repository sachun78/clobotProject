package com.lge.support.second.application.view.tabView

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.databinding.FragmentChatClickBinding
import com.lge.support.second.application.view.adapter.questionModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.lge.support.second.application.view.adapter.faqCustomAdapter

class chat_click : Fragment() {

    private var _binding: FragmentChatClickBinding? = null
    private val binding get() = _binding!!

    var click: Boolean = false

    var modelList = ArrayList<questionModel>()
    var questions = arrayOf(
        "무대", "좌석배치도", "오케스트라 피트","예술감독",
        "제작자", "무대감독, 음향감독, 조명감독", "음향반사판","하우스 매니저"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatClickBinding.inflate(inflater, container, false)

        var customAdapter =
            getActivity()?.getApplicationContext()?.let { faqCustomAdapter(modelList, it) }

        binding.clickGridView.adapter = customAdapter

        if (click == false) {
            for (i in questions.indices) {
                modelList.add(questionModel(questions[i]))
            }
            click = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickGridView.setOnItemClickListener { adapterView, view, i, l ->

            GlobalScope.launch {
                MainActivity.viewModel.getResponse(questions[i])
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
        Log.d("tk_test", "click destroy")
    }
}