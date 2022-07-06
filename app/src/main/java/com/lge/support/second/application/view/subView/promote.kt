package com.lge.support.second.application.view.subView

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.lge.support.second.application.R

class promote_normal(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////promote-normal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.normal)
    }
}

class promote_1(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////promote-normal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.promote_1)
    }
}

class promote_2(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////promote-normal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.promote_2)
    }
}

class promote_4(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////promote-normal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.promote_4)
    }
}