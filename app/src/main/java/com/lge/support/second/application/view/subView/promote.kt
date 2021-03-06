package com.lge.support.second.application.view.subView

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R

class promote_normal(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////promote-normal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.normal)

        MainActivity.movement_normal.show() //후면 promote_normal노출 시 전면 movement_normal
    }

    override fun hide() {
        super.hide()
        MainActivity.movement_normal.hide() //후면 promote_normal 꺼지면 전면 movement_normal도 hide
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