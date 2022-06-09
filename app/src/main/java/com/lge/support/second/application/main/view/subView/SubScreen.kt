package com.lge.support.second.application.main.view.subView

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import kr.co.clobot.robot.common.R

class subScreen(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub_test)
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}