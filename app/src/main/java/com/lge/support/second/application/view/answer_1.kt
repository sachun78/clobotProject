package com.lge.support.second.application.view

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnswer1Binding.inflate(inflater, container, false)

        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            /////////fragment??? this => getActivity().getApplicationContext()
            changeText(it.in_str)
            changeText2(it.speech[0])
            //Toast.makeText(activity as MainActivity, page_id, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    fun changeText(text: String?) {
        binding.answer1T1.setText(text)
    }

    fun changeText2(text: String?) {
        binding.answer1T2.setText(text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}