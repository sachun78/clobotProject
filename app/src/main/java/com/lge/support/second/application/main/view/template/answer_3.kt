package com.lge.support.second.application.main.view.template

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.ViewSwitcher
import androidx.core.net.toUri
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.MainActivity.Companion.urlArray
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswer3Binding
import kotlinx.coroutines.*
import java.net.URI
import java.net.URL
import java.util.ArrayList


class answer_3 : Fragment() {

    private var _binding: FragmentAnswer3Binding? = null
    private val binding get() = _binding!!

    lateinit var imageSwitcher: ImageSwitcher
    lateinit var btnNext: Button
    lateinit var btnBack: Button
    var position: Int = 0

    val imgArray = ArrayList<Bitmap>()
    val testArray = ArrayList<String>()

    val job0 = CoroutineScope(Dispatchers.Main)

    lateinit var testStr : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnswer3Binding.inflate(inflater, container, false)

        imageSwitcher = binding.answer3Is1
        btnNext = binding.answer3B2
        btnBack = binding.answer3B1

        //var drawable : Drawable = BitmapDrawable()

        imageSwitcher.setFactory {
            val imageView = ImageView(getActivity()?.getApplicationContext())
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView
        }

        //////////bitmap 이미지들을 모아둔 배열이 비어있다면
        if (MainActivity.BitmapArray.isNullOrEmpty()) {
            imgArray.clear()
            testArray.clear()
            job0.launch {
                for (i in 0..MainActivity.urlArray.size-1) {
                    val bitmap = withContext(Dispatchers.IO) {
                        loadImage(MainActivity.urlArray[position])
                    }
                    imgArray.add(bitmap)
                    Log.d("tk_test", "imgArray [$i] " + imgArray[i])

                    testStr = bitmap.toString().substring(17)
                    testArray.add(testStr)
                    Log.d("tk_test", "testArray [$i] " + testArray[i])
                }
//                val job1 = launch {
                    imageSwitcher.setImageDrawable(BitmapDrawable(imgArray[position]))
//                }
            }
        } else{
            imgArray.clear()
            for(i in 0..MainActivity.BitmapArray.size -1){
                imgArray.add(MainActivity.BitmapArray[i])
            }
//            imageSwitcher.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[position]))
            imageSwitcher.setImageDrawable(BitmapDrawable(imgArray[position]))
        }



        changeText(MainActivity.inStr)
        changeText2(MainActivity.speechStr)
        //imageSwitcher.setImageDrawable(BitmapDrawable(imgArray[position]))
//        for(i in 0..imgArray.size){
//            Log.d("tk_test", "imgArray[$i] is " + imgArray[i])
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {

            Log.d("tk_test", "back button click")

            if (position > 0) {
                position -= 1


                    imageSwitcher.setImageDrawable(BitmapDrawable(imgArray[position]))


            }
        }

        btnNext.setOnClickListener {
            Log.d("tk_test", "next button click")
            if (position < MainActivity.BitmapArray.size - 1) {
                position += 1
//                    imageSwitcher.setImageDrawable(BitmapDrawable(imgArray[position]))
            }
        }
    }

    fun changeText(text: String?) {
        binding.answer3T1.setText(text)
    }

    fun changeText2(text: String?) {
        binding.answer3T2.setText(text)
    }
}

suspend fun loadImage(imageUrl: String): Bitmap {
    val url = URL(imageUrl)
    val stream = url.openStream()

    return BitmapFactory.decodeStream(stream)
}