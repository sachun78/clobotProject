package com.lge.support.second.application.view

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.lge.support.second.application.R

class CustomProgressDialogue(context: Context) : Dialog(context) {
    init {
        val winParam: WindowManager.LayoutParams = window!!.attributes
        winParam.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = winParam
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.custom_progress, null
        )
        setContentView(view)
    }
}