package com.lge.support.second.application.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R


class information : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_information, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<ConstraintLayout>(R.id.background).setBackgroundResource(R.drawable.gongju_background_2)

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}