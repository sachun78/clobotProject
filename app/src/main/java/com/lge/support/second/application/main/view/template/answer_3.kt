package com.lge.support.second.application.main.view.template

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.ViewSwitcher
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswer3Binding


class answer_3 : Fragment() {

    private var _binding: FragmentAnswer3Binding? = null
    private val binding get() = _binding!!

    lateinit var imageSwitcher: ImageSwitcher
    lateinit var btnNext : Button
    lateinit var btnBack : Button
    var position : Int = 0

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

        imageSwitcher.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[position]))

        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {
            changeText(it.data.in_str)
            changeText2(it.data.result.fulfillment.speech[0])
            btnBack.setOnClickListener {
                if(position > 0){
                    position -= 1
                    imageSwitcher.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[position]))
                }
            }

            btnNext.setOnClickListener {
                if(position < MainActivity.BitmapArray.size-1) {
                    position += 1
                    imageSwitcher.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[position]))
                }
            }
        }

        return binding.root
    }

    fun changeText(text: String?){
        binding.answer3T1.setText(text)
    }
    fun changeText2(text: String?) {
        binding.answer3T2.setText(text)
    }

}