package com.lge.support.second.application.view.adapter

import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.view.chatView.chat
import com.lge.support.second.application.view.chatView.chat_fail
import com.lge.support.second.application.view.docent.docent_select
import com.lge.support.second.application.view.docent.move_docent
import com.lge.support.second.application.view.docent.test_docent
import com.lge.support.second.application.view.exhibits
import com.lge.support.second.application.view.information
import com.lge.support.second.application.view.location
import com.lge.support.second.application.view.subView.*
import com.lge.support.second.application.view.template.*
import com.lge.support.second.application.view.theater_map
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
            "emergency" -> {
                MainActivity.emergency_back.show()
                MainActivity.emergency_front.show()
            }
            "docking" -> {
                MainActivity.docking_screen.show()
            }
            "undocking" -> {
                MainActivity.undocking_screen.show()
            }
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
        "emergency" -> {
            MainActivity.emergency_back.hide()
            MainActivity.emergency_front.hide()
        }
        "docking" -> {
            MainActivity.docking_screen.hide()
        }
        "undocking" -> {
            MainActivity.undocking_screen.hide()
        }
    }
}

//fun showFrontScreen(showFront: String){ //후면 노출 시 -> 전면 ~~노출 => 후면 화면 show(), hide()에 넣기
//    when(showFront){
//        "movement-normal" -> {
//
//        }
//    }
//}
