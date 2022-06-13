package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R


class play : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_play, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.VISIBLE

        rootView.findViewById<ImageView>(R.id.play_i3).setOnClickListener {
            mActivity.changeFragment("play-clip")
        }

        return rootView
    }

}