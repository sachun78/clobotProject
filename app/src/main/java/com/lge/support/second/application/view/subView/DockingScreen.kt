package com.lge.support.second.application.view.subView

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R

class docking (outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.docking)
    }
}

class undocking (outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.undocking)
    }
}

class move_server(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.move_server)
    }
}

class move_charge(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.move_charge)
    }
}

class docking_guide(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.docking_guide)

        val moveBtn = findViewById<Button>(R.id.docking_guide_b1)

        moveBtn.setOnClickListener {
            MainActivity.robotViewModel.undockingRequest()
        }
    }
}

class emergency_screen(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
    //emergency
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emergency)

        val testBtn = findViewById<ImageView>(R.id.testEmergencyBtn)

        testBtn.setOnClickListener {

        }
    }
}

