package com.lge.support.second.application.view.tabView

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.databinding.FragmentChatKnowledgeBinding
import com.lge.support.second.application.view.adapter.questionModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.lge.support.second.application.view.adapter.faqCustomAdapter

class chat_knowledge : Fragment() {


    private var _binding: FragmentChatKnowledgeBinding? = null
    private val binding get() = _binding!!

    var click: Boolean = false

    var modelList = ArrayList<questionModel>()
    var questions = arrayOf(
        "국립극장공연이궁금해요", "question", "question","question",
        "question", "question", "question","question"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatKnowledgeBinding.inflate(inflater, container, false)

        var customAdapter =
            getActivity()?.getApplicationContext()?.let { faqCustomAdapter(modelList, it) }

        binding.knowledgeGridView.adapter = customAdapter

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
        binding.knowledgeGridView.setOnItemClickListener { adapterView, view, i, l ->
            GlobalScope.launch {
                MainActivity.viewModel.getResponse(questions[i])
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
        Log.d("tk_test", "knowledge destroy")
    }
}