package com.lge.support.second.application.view.subView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentImagePopupBinding
import com.lge.support.second.application.view.template.answer_location


class image_popup : Fragment() {

    private lateinit var binding : FragmentImagePopupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImagePopupBinding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.GONE

        Glide.with(this).load(answer_location.arrImage[answer_location.position])
            .into(binding.popUpImg)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.popUpimgCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.VISIBLE
    }
}