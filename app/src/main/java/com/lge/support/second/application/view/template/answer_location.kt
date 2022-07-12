package com.lge.support.second.application.view.template

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswerLocationBinding
import com.lge.support.second.application.model.MainViewModel
import com.lge.support.second.application.model.RobotViewModel
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

    val viewModel by activityViewModels<MainViewModel>()
    private val robotViewModel by activityViewModels<RobotViewModel>()

    //공통 사용
    lateinit var btnNext: Button
    lateinit var btnBack: Button

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        lifecycleScope.launch(Dispatchers.IO) {
//            MainActivity.viewModel.queryResult.value?.in_str?.let {
//                MainActivity.viewModel.getResponse(
//                    it,
//                    "query"
//                )
//            }
//        }
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

        ////////챗봇 연동 되면 바꿀 부분 !!!!!!!!!!!!!!!! 1. 주석 풀기
        //binding.answerLocationC1.text = exhibits.nameList[position].text

        imageSwitcher.setFactory {
            var imageView = ImageView(activity?.applicationContext)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView
        }

        ////////////Main -> urlArray2[ position ]
        arrImage.clear()
        ////////챗봇 연동 되면 바꿀 부분 !!!!!!!!!!!!!!!!
        //1. 주석 풀기 2.urlArray2->urlArray로.. 3.수동으로 add해준 것 없애기
//        for (imgUrl in MainActivity.urlArray2) {
//            arrImage.add(imgUrl)
//        }
        arrImage.add("https://newsimg.sedaily.com/2020/12/01/1ZBIJQNGIG_1.jpg")
        arrImage.add("https://dimg.donga.com/wps/NEWS/IMAGE/2020/08/26/102662103.1.jpg")
        arrImage.add("http://image.dongascience.com/Photo/2020/03/32ee9fae42b5b0a8f466d61ba2acc281.jpg")

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
                ////////챗봇 연동 되면 바꿀 부분 !!!!!!!!!!!!!!!! 1. 주석 풀기
                //binding.answerLocationC1.text = exhibits.nameList[position].text
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
                ////////챗봇 연동 되면 바꿀 부분 !!!!!!!!!!!!!!!! 1. 주석 풀기
                //binding.answerLocationC1.text = exhibits.nameList[position].text
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

        binding.testDocentB1.setOnClickListener {
            Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
            viewModel.ttsStop()
            robotViewModel.docentRequest(activity as MainActivity, 3)

        }

        return binding.root
    }
}