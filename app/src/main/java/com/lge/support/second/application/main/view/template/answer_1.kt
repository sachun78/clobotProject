package com.lge.support.second.application.main.view.template

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        //MainActivity.viewModel.getResponse("하이 큐아이")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {
            /////////fragment??? this => getActivity().getApplicationContext()
            //GoogleTTS.speak(getActivity()?.getApplicationContext(), it.data.result.fulfillment.speech[0])
            changeText(it.data.in_str)
            changeText2(it.data.result.fulfillment.speech[0])
            //Toast.makeText(activity as MainActivity, page_id, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.stop()
    }

    fun changeText(text: String?){
        binding.answer1T1.setText(text)
    }
    fun changeText2(text: String?) {
        binding.answer1T2.setText(text)
    }

}