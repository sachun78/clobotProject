package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.google.api.Distribution
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R

class docent_end : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_docent_end, container, false)

        val mActivity = activity as MainActivity

        mActivity.findViewById<LinearLayout>(R.id.top).visibility = View.GONE

        rootView.findViewById<Button>(R.id.end_b4).setOnClickListener {
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        return rootView
    }


}