package com.lge.support.second.application.view.template

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswer3Binding
import java.net.URL
import java.util.ArrayList


class answer_3 : Fragment() {

    private var _binding: FragmentAnswer3Binding? = null
    private val binding get() = _binding!!

    lateinit var imageSwitcher: ImageView

    //lateinit var imageSwitcher: ImageSwitcher
    lateinit var btnNext: Button
    lateinit var btnBack: Button
    var position: Int = 0

    val imgArray = ArrayList<Bitmap>()

    val questionStr = MainActivity.inStr
    val answerStr = MainActivity.speechStr

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnswer3Binding.inflate(inflater, container, false)

        imageSwitcher = binding.answer3Is1
        btnNext = binding.answer3B2
        btnBack = binding.answer3B1

        val mActivity = activity as MainActivity
        mActivity.findViewById<ImageView>(R.id.qrImg).visibility = View.GONE

        //////////bitmap 이미지들을 모아둔 배열이 비어있다면
        if (MainActivity.BitmapArray.isNullOrEmpty()) {
            imgArray.clear()
            Glide.with(this).load(MainActivity.urlArray[position]).into(imageSwitcher)
        }
        else {
            imageSwitcher.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[position]))
        }

        binding.answer3T1.setText(questionStr)
        binding.answer3T2.setText(answerStr)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            if (MainActivity.BitmapArray.isNullOrEmpty()) {
                if (position < MainActivity.urlArray.size - 1) {
                    position += 1
                    Glide.with(this).load(MainActivity.urlArray[position]).into(imageSwitcher)
                }
            }
            else {
                if (position < MainActivity.BitmapArray.size - 1) {
                    position += 1

                    imageSwitcher.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[position]))

                }
            }
        }

        btnNext.setOnClickListener {
            if (MainActivity.BitmapArray.isNullOrEmpty()) {
                if (position < MainActivity.urlArray.size - 1) {
                    position += 1
                    Glide.with(this).load(MainActivity.urlArray[position]).into(imageSwitcher)
                }
            }
            else {
                if (position < MainActivity.BitmapArray.size - 1) {
                    position += 1

                    imageSwitcher.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[position]))

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}

suspend fun loadImage(imageUrl: String): Bitmap {
    val url = URL(imageUrl)
    val stream = url.openStream()

    return BitmapFactory.decodeStream(stream)
}