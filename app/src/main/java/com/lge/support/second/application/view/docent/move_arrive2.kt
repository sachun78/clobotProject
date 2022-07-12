package com.lge.support.second.application.view.docent

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentMoveArrive2Binding
import com.lge.support.second.application.view.adapter.currentBackScreen
import com.lge.support.second.application.view.adapter.hideBackScreen

class move_arrive2 : Fragment() {

    private lateinit var binding: FragmentMoveArrive2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoveArrive2Binding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.GONE
        hideBackScreen(currentBackScreen)

        binding.moveArrive2B1.setOnClickListener {
            val builder = AlertDialog.Builder(context)
//            builder.setTitle("프로그램 ")
            builder.setMessage("제자리 해설 듣기?").setPositiveButton("yes",
                DialogInterface.OnClickListener { dialog, id ->
                    Log.d(
                        "move_arrive_2",
                        "yes click"
                    )
                    (activity as MainActivity).changeFragment("docent-play")
                })
                .setNegativeButton("no",
                    DialogInterface.OnClickListener { dialog, id ->
                        Log.d(
                            "move_arrive_2",
                            "no click"
                        )
                    })
            builder.show()
        }

        binding.moveArrive2B3.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.moveArrive2B4.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.VISIBLE
    }
}