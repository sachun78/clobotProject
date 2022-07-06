package com.lge.support.second.application.view.docent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentDocentSelectBinding


class docent_select : Fragment() {

    private lateinit var binding: FragmentDocentSelectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDocentSelectBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        mActivity.findViewById<ConstraintLayout>(R.id.background).setBackgroundResource(R.drawable.gongju_background_4)
        mActivity.findViewById<Button>(R.id.homeBtn).visibility = View.INVISIBLE
        mActivity.findViewById<Button>(R.id.micBtn).visibility = View.INVISIBLE
        mActivity.findViewById<ImageView>(R.id.gnbIcon).visibility = View.INVISIBLE
        mActivity.findViewById<Button>(R.id.korBtn).visibility = View.INVISIBLE
        mActivity.findViewById<Button>(R.id.enBtn).visibility = View.INVISIBLE
        mActivity.findViewById<Button>(R.id.chiBtn).visibility = View.INVISIBLE
        mActivity.findViewById<Button>(R.id.jpnBtn).visibility = View.INVISIBLE

        //        mActivity.findViewById<Button>(R.id.backBtn).setBackgroundResource(R.drawable.back_main)
        //        mActivity.findViewById<Button>(R.id.homeBtn).setBackgroundResource(R.drawable.home_main)

        binding.listenAllBtn.setOnClickListener {
            MainActivity.robotViewModel.docent1Request(mActivity)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val mActivity = activity as MainActivity

        mActivity.findViewById<Button>(R.id.homeBtn).visibility = View.VISIBLE
        mActivity.findViewById<Button>(R.id.micBtn).visibility = View.VISIBLE
        mActivity.findViewById<ImageView>(R.id.gnbIcon).visibility = View.VISIBLE
        mActivity.findViewById<Button>(R.id.korBtn).visibility = View.VISIBLE
        mActivity.findViewById<Button>(R.id.enBtn).visibility = View.VISIBLE
        mActivity.findViewById<Button>(R.id.chiBtn).visibility = View.VISIBLE
        mActivity.findViewById<Button>(R.id.jpnBtn).visibility = View.VISIBLE
    }
}