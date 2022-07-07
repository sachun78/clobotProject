package com.lge.support.second.application.view.template

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.databinding.FragmentAnswer2Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class answer_2 : Fragment() {

    private lateinit var binding: FragmentAnswer2Binding

    val questionStr = MainActivity.inStr
    val answerStr = MainActivity.speechStr
    val descriptStr = MainActivity.descriptStr

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnswer2Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.answer2T1.setText(questionStr)
        binding.answer2T2.setText(answerStr)
        binding.answer2T3.setText(descriptStr)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }

}