package com.lge.support.second.application.view.docent

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
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
        MainActivity.subVideo.hide()

        //다시듣기
        rootView.findViewById<Button>(R.id.end_b1).setOnClickListener {
            fragmentManager?.popBackStack()
            MainActivity.subTest.findViewById<TextView>(R.id.sub_textView).setText("")
        }

        //종료 하기
        rootView.findViewById<Button>(R.id.end_b4).setOnClickListener {
            //fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            var dialog = Dialog(mActivity)
            dialog.setContentView(R.layout.docent_end_dialog_layout)
            dialog.show()

            dialog.findViewById<Button>(R.id.yes_btn).setOnClickListener {
                dialog.hide()
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                MainActivity.subTest.findViewById<TextView>(R.id.sub_textView).setText("")
            }

            dialog.findViewById<Button>(R.id.no_btn).setOnClickListener {
                dialog.hide()
            }
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}