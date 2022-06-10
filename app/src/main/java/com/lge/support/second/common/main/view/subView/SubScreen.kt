package com.lge.support.second.application.main.view.subView

import android.app.Activity
import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.MainActivity.Companion.tkTestViewModel
import com.lge.support.second.application.R


class SubScreen(outerContext: Context?, display: Display?) : Presentation(outerContext, display){
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