package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R


class play : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mActivity = activity as MainActivity
        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.VISIBLE

        return inflater.inflate(R.layout.fragment_play, container, false)
    }

}