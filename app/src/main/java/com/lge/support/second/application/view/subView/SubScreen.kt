package com.lge.support.second.application.view.subView

import android.annotation.SuppressLint
import android.app.Presentation
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.data.camera.FaceContourDetectionProcessor
import com.lge.support.second.application.databinding.StandbyBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SubScreen(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub_test)
    }
}

class standby(outerContext: Context?, display: Display?) : Presentation(outerContext, display),
    LifecycleOwner {
    private var imageCapture: ImageCapture? = null
    private lateinit var viewBinding: StandbyBinding
    private lateinit var lifecycleRegistry: LifecycleRegistry
    private var imageAnalyzer: ImageAnalysis? = null
    lateinit var metrics: DisplayMetrics

    private lateinit var cameraExecutor: ExecutorService

    init {
        createNewExecutor()
    }

    private fun createNewExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun hide() {
        super.hide()
        cameraExecutor.shutdown()
    }

    override fun show() {
        super.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = StandbyBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        startCamera()
//        //일반 standby 상황 text
//        textView1.setText("도움이 필요하신가요?")
        //textView2.setText("제가 도와드리겠습니다.")

        //docent - standby 상황 text 다르게 setting
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var action = event.action

        if (action == MotionEvent.ACTION_DOWN) {
            hide()
        }
        return super.onTouchEvent(event)
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, FaceContourDetectionProcessor())
                    it.targetRotation = Surface.ROTATION_90
                }

            metrics = DisplayMetrics().also { viewBinding.previewView.display.getRealMetrics(it) }

            imageCapture =
                ImageCapture.Builder()
                    .setTargetResolution(Size(metrics.widthPixels, metrics.heightPixels))
                    .build().also {
                        it.targetRotation = Surface.ROTATION_90
                    }

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.previewView.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA
            //.requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e("CAMERA", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
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

            var uri =
                Uri.parse("android.resource://" + "com.lge.support.second.application" + "/raw/docent_10_")

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
        MainActivity.subTest.findViewById<TextView>(R.id.sub_textView).setText("문화해설 서비스를 이용중..")
    }
}

//////////////////////////////////////////////////docent//////////////////////////////////////////////////
class moveNormal(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////promote-normal, movement-normal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.normal)
    }
}

class moveDocent(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////move-docent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.move_docent)
    }
}

class docent_back(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {
    /////////////////docent_back
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.move_docent)
    }
}