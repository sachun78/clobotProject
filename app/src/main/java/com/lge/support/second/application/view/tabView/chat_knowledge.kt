package com.lge.support.second.application.view.tabView

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
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

    lateinit var exhibits1 : String
    lateinit var exhibits2 : String
    lateinit var exhibits3 : String
    lateinit var exhibits4 : String
    lateinit var exhibits5 : String
    lateinit var exhibits6 : String
    lateinit var exhibits7 : String
    lateinit var exhibits8 : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatKnowledgeBinding.inflate(inflater, container, false)

        var customAdapter =
            getActivity()?.getApplicationContext()?.let { faqCustomAdapter(modelList, it) }

        binding.knowledgeGridView.adapter = customAdapter

        exhibits1 = resources.getString(R.string.chat_exhibits_b4)
        exhibits2 = resources.getString(R.string.chat_exhibits_b5)
        exhibits3 = resources.getString(R.string.chat_exhibits_b6)
        exhibits4 = resources.getString(R.string.chat_exhibits_b7)
        exhibits5 = resources.getString(R.string.chat_exhibits_b8)
        exhibits6 = resources.getString(R.string.chat_exhibits_b9)
        exhibits7 = resources.getString(R.string.chat_exhibits_b10)
        exhibits8 = resources.getString(R.string.chat_exhibits_b11)

        var questions = arrayOf(
            exhibits1 , exhibits2, exhibits3 , exhibits4,exhibits5, exhibits6, exhibits7, exhibits8
        )

        if (click == false) {
            for (i in questions.indices) {
                modelList.add(questionModel(questions[i]))
            }
            click = true
        }

        binding.knowledgeGridView.setOnItemClickListener { adapterView, view, i, l ->
            lifecycleScope.launch {
                MainActivity.viewModel.getResponse(questions[i])
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
        Log.d("tk_test", "knowledge destroy")
    }
}