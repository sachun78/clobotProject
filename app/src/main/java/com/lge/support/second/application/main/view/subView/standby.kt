package com.lge.support.second.application.main.view.subView

import android.app.Activity
import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import kotlinx.coroutines.delay


class standby(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.standby)

        var textView1 = findViewById<TextView>(R.id.standby_t1)
        var textView2 = findViewById<TextView>(R.id.standby_t1)
//
//        //일반 standby 상황 text
//        textView1.setText("도움이 필요하신가요?")

        //textView2.setText("제가 도와드리겠습니다.")

        //docent - standby 상황 text 다르게 setting

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