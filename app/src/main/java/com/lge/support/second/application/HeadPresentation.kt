package com.lge.support.second.application

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import com.lge.support.second.application.databinding.PresentationHeadBinding

/**
 * A simple [Fragment] subclass.
 * Use the [HeadPresentation.newInstance] factory method to
 * create an instance of this fragment.
 */
class HeadPresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {
    private var _binding: PresentationHeadBinding? = null
    lateinit var text1: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = PresentationHeadBinding.inflate(layoutInflater)
        setContentView(R.layout.presentation_head)

        text1 = findViewById<View>(R.id.textView2) as TextView
        println(text1)
    }

    fun changeText(text: String?) {
        text1.text = text
    }
}