package com.lge.support.second.application.view.template

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.databinding.FragmentAnswer2Binding

class answer_2 : Fragment() {

    private var _binding: FragmentAnswer2Binding? = null
    private val binding get() = _binding!!

    val questionStr = MainActivity.inStr
    val answerStr = MainActivity.speechStr
    val descriptStr = MainActivity.descriptStr

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnswer2Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        return binding.root
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