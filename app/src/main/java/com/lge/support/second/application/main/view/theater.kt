package com.lge.support.second.application.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R

class theater : Fragment() {
    //    private var _binding: FragmentTheaterBinding? = null
//    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_theater, container, false)

        val mActivity = activity as MainActivity
        val menu1 = rootView.findViewById<ImageView>(R.id.theater_i1)

//        mActivity.changeVisibility(1)
        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.VISIBLE

        menu1.setOnClickListener {
            mActivity.changeFragment(21)
        }

        rootView.findViewById<ImageView>(R.id.theater_i2).setOnClickListener {
            mActivity.changeFragment(22)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}