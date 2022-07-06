package com.lge.support.second.application.view.subView

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.MotionEvent
import com.lge.support.second.application.R

class standby(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.standby)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        var action = event.getAction()

        if(action == MotionEvent.ACTION_DOWN) {
            hide()
        }
        return super.onTouchEvent(event)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()

    }
}

class docent_standby(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.docent_standby)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        var action = event.getAction()

        if(action == MotionEvent.ACTION_DOWN) {
            hide()
        }
        return super.onTouchEvent(event)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()

    }
}

class move_standby(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.move_standby)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        var action = event.getAction()

        if(action == MotionEvent.ACTION_DOWN) {
            hide()
        }
        return super.onTouchEvent(event)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()

    }
}
