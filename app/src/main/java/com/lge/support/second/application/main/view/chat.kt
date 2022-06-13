package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentChatBinding


class chat : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridView = binding.chatGridView
        val numbers = arrayOf("Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "다른 질문 고르기")

        val NumberAdp : ArrayAdapter<String> = ArrayAdapter(activity as MainActivity, android.R.layout.simple_list_item_1, numbers)

        gridView.adapter = NumberAdp

        gridView.setOnItemClickListener({
                parentFragment, view, position, id ->
            Toast.makeText(activity as MainActivity, "questionClick:-$id", Toast.LENGTH_SHORT).show()
        })

        binding.chatI1.setOnClickListener {
            (activity as MainActivity).changeFragment("answer-1")
        }
    }

}