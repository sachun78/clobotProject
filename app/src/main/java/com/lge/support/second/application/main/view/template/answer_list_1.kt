package com.lge.support.second.application.main.view.template

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswerList1Binding


class answer_list_1 : Fragment() {

    private var _binding: FragmentAnswerList1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnswerList1Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {
            changeText(it.data.in_str)
            changeText2(it.data.result.fulfillment.speech[0])
        }
    }

    fun changeText(text: String?){
        binding.answerList1T1.setText(text)
    }
    fun changeText2(text: String?) {
        binding.answerList1T2.setText(text)
    }
}