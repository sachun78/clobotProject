package com.lge.support.second.application.view.template

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswerLocationBinding
import com.lge.support.second.application.view.exhibits
import com.lge.support.second.application.view.subView.image_popup
import com.lge.support.second.application.view.subView.threeD_popup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class answer_location : Fragment() {

    private lateinit var binding: FragmentAnswerLocationBinding

    //이미지 부분
    lateinit var imageSwitcher: ImageSwitcher

    //    lateinit var arrImage : IntArray
    companion object {
        var arrImage = ArrayList<String>()
        var position: Int = 0
    }

    //공통 사용
    lateinit var btnNext: Button
    lateinit var btnBack: Button

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            MainActivity.viewModel.queryResult.value?.in_str?.let {
                MainActivity.viewModel.getResponse(
                    it,
                    "query"
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnswerLocationBinding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<ConstraintLayout>(R.id.background)
            .setBackgroundResource(R.drawable.gongju_background_3)

        binding.answerLocationC4.movementMethod

        imageSwitcher = binding.answerLocationC3
        btnNext = binding.answerLocNext
        btnBack = binding.answerLocBack

        binding.answerLocationC1.text = exhibits.nameList[position].text

        imageSwitcher.setFactory {
            var imageView = ImageView(activity?.applicationContext)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView
        }

        ////////////Main -> urlArray2[ position ]
        arrImage.clear()
        for (imgUrl in MainActivity.urlArray2) {
            arrImage.add(imgUrl)
        }

        //첫 화면 세팅
        Glide.with(imageSwitcher.context).load(arrImage[position])
            .into(imageSwitcher.currentView as ImageView)
        if (position == 0)
            btnBack.visibility = View.INVISIBLE
        if (position == arrImage.size - 1)
            btnNext.visibility = View.INVISIBLE

        //button click listener
        btnBack.setOnClickListener { /////////Main -> urlArray[position]
            if (position > 0) {
                btnNext.visibility = View.VISIBLE
                position -= 1
                if (position == 0)
                    btnBack.visibility = View.INVISIBLE
                Glide.with(imageSwitcher.context).load(arrImage[position])
                    .into(imageSwitcher.currentView as ImageView)
                binding.answerLocationC1.text = exhibits.nameList[position].text
            }
            if (position == 0)
                btnBack.visibility = View.INVISIBLE
        }
        btnNext.setOnClickListener {
            if (position < arrImage.size - 1) {
                btnBack.visibility = View.VISIBLE
                position += 1
                Glide.with(imageSwitcher.context).load(arrImage[position])
                    .into(imageSwitcher.currentView as ImageView)
                binding.answerLocationC1.text = exhibits.nameList[position].text
            }

            if (position == arrImage.size - 1) {
                btnNext.visibility = View.INVISIBLE
            }
        }

        binding.answerLocationB6.setOnClickListener {
//            parentFragment..replace(R.id.fragment_main, answer_location())
//                .addToBackStack(null).commit()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_main, threeD_popup())
                .addToBackStack(null).commit()
        }

        binding.answerLocationB7.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_main, image_popup())
                .addToBackStack(null).commit()
        }
        return binding.root
    }
}