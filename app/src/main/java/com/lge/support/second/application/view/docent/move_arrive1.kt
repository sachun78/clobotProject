package com.lge.support.second.application.view.docent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentMoveArrive1Binding


class move_arrive1 : Fragment() {

    private lateinit var binding: FragmentMoveArrive1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoveArrive1Binding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.GONE

        binding.moveArrive1B2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.moveArrive1B3.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.VISIBLE
    }
}