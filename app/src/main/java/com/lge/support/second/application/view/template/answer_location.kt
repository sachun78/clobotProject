package com.lge.support.second.application.view.template

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswerLocationBinding
import com.lge.support.second.application.view.subView.image_popup
import com.lge.support.second.application.view.subView.threeD_popup


class answer_location : Fragment() {

    private lateinit var binding: FragmentAnswerLocationBinding

    //이미지 부분
    lateinit var imageSwitcher: ImageSwitcher

    //    lateinit var arrImage : IntArray
    companion object{
        var arrImage = ArrayList<String>()
        var position: Int = 0
    }

    //공통 사용
    lateinit var btnNext: Button
    lateinit var btnBack: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnswerLocationBinding.inflate(inflater, container, false)

        binding.answerLocationC4.movementMethod

        imageSwitcher = binding.answerLocationC3
        btnNext = binding.answerLocNext
        btnBack = binding.answerLocBack

//        arrImage = MainActivity.urlArray

        imageSwitcher.setFactory {
            var imageView = ImageView(getActivity()?.getApplicationContext())
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView
        }

        arrImage.add("https://ichef.bbci.co.uk/news/640/cpsprodpb/14C73/production/_121170158_planepoogettyimages-1135673520.jpg")
        arrImage.add("https://greenium.kr/wp-content/uploads/2021/06/%EB%B9%84%ED%96%89%EA%B8%B0-1.jpg")
        arrImage.add("https://www.ekn.kr/mnt/file/202110/2021101201000367900015741.jpg")

        ////////////Main -> urlArray[0]
//        arrImage.clear()
//        for(imgUrl in MainActivity.urlArray){
//            arrImage.add(imgUrl)
//        }

        //첫 화면 세팅
        Glide.with(imageSwitcher.context).load(arrImage[0])
            .into(imageSwitcher.currentView as ImageView)
        btnBack.visibility = View.INVISIBLE
        if(position == arrImage.size-1)
            btnNext.visibility = View.INVISIBLE

        //button click listener
        btnBack.setOnClickListener { /////////Main -> urlArray[position]
            if (position > 0) {
                btnNext.visibility = View.VISIBLE
                position -= 1
                if(position == 0)
                    btnBack.visibility = View.INVISIBLE
                Glide.with(imageSwitcher.context).load(arrImage[position])
                    .into(imageSwitcher.currentView as ImageView)
            }
            if(position == 0)
                btnBack.visibility = View.INVISIBLE
        }
        btnNext.setOnClickListener {
            if (position < arrImage.size - 1) {
                btnBack.visibility = View.VISIBLE
                position += 1
                Glide.with(imageSwitcher.context).load(arrImage[position])
                    .into(imageSwitcher.currentView as ImageView)
            }

            if(position == arrImage.size - 1){
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