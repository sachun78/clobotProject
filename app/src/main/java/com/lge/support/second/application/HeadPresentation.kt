package com.lge.support.second.application

import android.app.Presentation
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.fragment.app.Fragment
import com.lge.support.second.application.databinding.PresentationHeadBinding

/**
 * A simple [Fragment] subclass.
 * Use the [HeadPresentation.newInstance] factory method to
 * create an instance of this fragment.
 */
class HeadPresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display), SurfaceHolder.Callback {
    private var _binding: PresentationHeadBinding? = null

    var surfaceView: SurfaceView? = null
    var surfaceHolder: SurfaceHolder? = null
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = PresentationHeadBinding.inflate(layoutInflater)
        setContentView(R.layout.presentation_head)

        mediaPlayer = MediaPlayer()

        surfaceView = findViewById(R.id.head_surfaceView)
        surfaceHolder = surfaceView?.holder
        surfaceHolder?.addCallback(this);
    }

    @Deprecated(message = "not use text on head!")
    fun changeText(text: String?) {
//        text1.text = text
    }

    fun changeExpression(exp: Expression) {
        mediaPlayer.reset()
        when (exp) {
            Expression.CURIOUS -> {
                val afd: AssetFileDescriptor = context.assets.openFd("face_type_curious.mp4")
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            Expression.HAPPY -> {
                val afd: AssetFileDescriptor = context.assets.openFd("face_type_happy.mp4")
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            else -> {
                val afd: AssetFileDescriptor = context.assets.openFd("face_type_wink.mp4")
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
        }

        mediaPlayer.prepare() // 비디오 load 준비
        mediaPlayer.start()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        try {
            val afd: AssetFileDescriptor = context.assets.openFd("face_type_wink.mp4")
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.setOnPreparedListener { mp ->
                mp.isLooping = true
            }

            mediaPlayer.setDisplay(surfaceHolder) // 화면 호출
            mediaPlayer.prepare() // 비디오 load 준비
            mediaPlayer.start()

        } catch (e: Exception) {
            Log.e("HEAD", "surface view error : " + e.message)
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        Log.e("HEAD", "surface changed!")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        mediaPlayer.release()
    }
}

enum class Expression {
    WINK, CURIOUS, HAPPY
}