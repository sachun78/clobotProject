package com.lge.support.second.application.main.view.docent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R

class move_docent : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_move_docent, container, false)

        val mActivity = activity as MainActivity

        mActivity.findViewById<LinearLayout>(R.id.top).visibility = View.GONE
        mActivity.findViewById<ImageView>(R.id.qrImg).visibility = View.GONE

        rootView.findViewById<Button>(R.id.move_docent_b1).setOnClickListener {
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        return rootView
    }

}