package com.lge.support.second.application.view.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.databinding.FragmentAnswer1Binding


class answer_1 : Fragment() {

    private var _binding: FragmentAnswer1Binding? = null
    private val binding get() = _binding!!

    val questionStr = MainActivity.inStr
    val answerStr = MainActivity.speechStr

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnswer1Binding.inflate(inflater, container, false)

        binding.answer1T1.setText(questionStr)
        binding.answer1T2.setText(answerStr)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}