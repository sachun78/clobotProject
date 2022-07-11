package com.lge.support.second.application.view.subView

import android.annotation.SuppressLint
import android.app.Presentation
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.view.docent.docent_select
import org.apache.log4j.chainsaw.Main


class information_back(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub_test)
    }

    override fun show() {
        super.show()
        Log.d("information_back", "show")
    }

    override fun hide() {
        super.hide()
        Log.d("information_back", "hide")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}

class back_video(outerContext: Context?, display: Display?) : Presentation(outerContext, display),
    SurfaceHolder.Callback {
    var surfaceView: SurfaceView? = null
    var surfaceHolder: SurfaceHolder? = null
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_move_docent)

        surfaceView = findViewById(R.id.surface_view)
        surfaceHolder = surfaceView?.holder
        surfaceHolder?.addCallback(this);
    }

    @SuppressLint("RestrictedApi")
    override fun surfaceCreated(p0: SurfaceHolder) {
        //if (mediaPlayer == null) {
        mediaPlayer = MediaPlayer()
//        } else {
//            mediaPlayer!!.reset()
//        }

        try {
//            val path =
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

//            var uri =
//                Uri.parse("android.resource://" + "com.lge.support.second.application" + "/raw/docent_10_")
            var uri = Uri.parse(docent_select.uri)

            Log.d("uri check", uri.toString())

//            mediaPlayer!!.setVolume(0F, 0F) //볼륨 제거
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.setDisplay(surfaceHolder) // 화면 호출
            mediaPlayer.prepare() // 비디오 load 준비
            mediaPlayer.setOnCompletionListener { p0 ->
                println("onCompletion!")
                p0?.release()
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_main, docent_end())
//                    .addToBackStack(null).commit()
            }; // 비디오 재생 완료 리스너

            mediaPlayer.setVolume(0F, 0F) //볼륨 제거
            mediaPlayer.start()
        } catch (e: Exception) {

        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        mediaPlayer.release()
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun hide() {
        super.hide()
//        MainActivity.subTest.findViewById<TextView>(R.id.sub_textView).setText("문화 해설 서비스 중입니다.")
    }
}

//////////////////////////////////////////////////docent//////////////////////////////////////////////////
class movement_normal(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {
    /////////////////promote-normal, movement-normal(front)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.normal)
    }
}

class moveDocent(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////move-docent(back)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.move_docent)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}

class docent_back(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////docent_back(back)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.docent_back)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}

