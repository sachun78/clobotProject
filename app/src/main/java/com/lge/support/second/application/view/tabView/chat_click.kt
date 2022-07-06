package com.lge.support.second.application.view.tabView

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
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

    lateinit var guide1 : String
    lateinit var guide2 : String
    lateinit var guide3 : String
    lateinit var guide4 : String
    lateinit var guide5 : String
    lateinit var guide6 : String
    lateinit var guide7 : String
    lateinit var guide8 : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatClickBinding.inflate(inflater, container, false)

        var customAdapter =
            getActivity()?.getApplicationContext()?.let { faqCustomAdapter(modelList, it) }

        binding.clickGridView.adapter = customAdapter

        guide1 = resources.getString(R.string.chat_guide_b4)
        guide2 = resources.getString(R.string.chat_guide_b5)
        guide3 = resources.getString(R.string.chat_guide_b6)
        guide4 = resources.getString(R.string.chat_guide_b7)
        guide5 = resources.getString(R.string.chat_guide_b8)
        guide6 = resources.getString(R.string.chat_guide_b9)
        guide7 = resources.getString(R.string.chat_guide_b10)
        guide8 = resources.getString(R.string.chat_guide_b11)

        var questions = arrayOf(
            guide1 , guide2, guide3, guide4, guide5 , guide6 , guide7 , guide8 )

        if (click == false) {
            for (i in questions.indices) {
                modelList.add(questionModel(questions[i]))
            }
            click = true
        }

        binding.clickGridView.setOnItemClickListener { adapterView, view, i, l ->

            GlobalScope.launch {
                MainActivity.viewModel.getResponse(questions[i])
            }
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
        Log.d("tk_test", "click destroy")
    }
}