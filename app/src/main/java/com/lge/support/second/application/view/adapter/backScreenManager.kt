package com.lge.support.second.application.view.adapter

import android.util.Log
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.view.subView.*
import org.apache.log4j.chainsaw.Main

lateinit var currentBackScreen: String


fun showBackScreen(showBack: String) { /////////////screen 1번(backScreen)
    if (currentBackScreen != showBack) {
        hideBackScreen(currentBackScreen)
        currentBackScreen = showBack
        when (showBack) {
            "docent_back" -> {
                MainActivity.docent_back.show()
            }
            "move_docent" -> {
                MainActivity.move_docent.show()
            }
            "promote_normal" -> {
                MainActivity.promote_normal.show()
            }
            "move_arrive1" ->
                MainActivity.moveArrive1.show()
        }
    }
}

fun hideBackScreen(hideBack: String) {
    when (hideBack) {
        "docent_back" -> {
            MainActivity.docent_back.hide()
        }
        "move_docent" -> {
            MainActivity.move_docent.hide()
        }
        "promote_normal" -> {
            MainActivity.promote_normal.hide()
        }
        "move_arrive1" ->
            MainActivity.moveArrive1.hide()
    }
}