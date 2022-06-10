package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R


class enjoy : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_enjoy, container, false)

        val mActivity = activity as MainActivity
        val testBtn = rootView.findViewById<ImageView>(R.id.enjoy_i1)

//        testBtn.setOnClickListener {
//            mActivity.changeFragment(31)
//        }

        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.VISIBLE

        return rootView
    }

}