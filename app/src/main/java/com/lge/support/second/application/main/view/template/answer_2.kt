package com.lge.support.second.application.main.view.template

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswer2Binding

class answer_2 : Fragment() {

    private var _binding: FragmentAnswer2Binding? = null
    private val binding get() = _binding!!

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
        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {
            changeText(it.data.in_str)
            changeText2(it.data.result.fulfillment.speech[0])
            changeText3(it.data.result.fulfillment.messages[0].description[0].text)
        }
    }

    fun changeText(text: String?){
        binding.answer2T1.setText(text)
    }
    fun changeText2(text: String?) {
        binding.answer2T2.setText(text)
    }
    fun changeText3(text:String?) {
        binding.answer2T3.setText(text)
    }
}