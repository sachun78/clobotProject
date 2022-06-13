package com.lge.support.second.application.main.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.databinding.FragmentAnswer1Binding

class answer_1 : Fragment() {
    private var _binding: FragmentAnswer1Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnswer1Binding.inflate(inflater, container, false)

        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {
            Log.d("ViewModel", "chatbot data change, $it")
            if (it == null) {
                return@observe
            }
            /////////fragment??? this => getActivity().getApplicationContext()
            activity?.getApplicationContext()
                ?.let { it1 ->
                    MainActivity.viewModel.speak(
                        it1,
                        it.data.result.fulfillment.speech[0]
                    )
                }
            changeText(it.data.result.fulfillment.speech[0] + " (" + it.data.result.fulfillment.custom_code.head + ")")
            it.data.result.fulfillment.messages.forEach { message ->
                Log.d("ViewModel Observe", message.image.toString())
            }
        }

        return binding.root
    }

    fun changeText(text: String?) {
        binding.answer1T.setText(text)
    }
}