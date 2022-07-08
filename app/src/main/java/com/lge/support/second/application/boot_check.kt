package com.lge.support.second.application

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class boot_check : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 5000L

    init {
        instance = this
    }

    companion object {
        lateinit var instance: boot_check

        fun bootCheckContext(): Context {
            return instance
        }

        private val PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private const val REQUEST_CODE_PERMISSION = 200

        //language
        lateinit var langPref : SharedPreferences
        lateinit var editor: SharedPreferences.Editor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot_check) //activity_boot_check

        langPref = getSharedPreferences("My_Lang", Activity.MODE_PRIVATE)
        editor = langPref.edit()

        // CHECK PERMISSIONS
        if (allPermissionsGranted()) {
            //standby.show()
            Log.d("grant check", "all granted")
        } else {
            ActivityCompat.requestPermissions(
                this, PERMISSIONS, REQUEST_CODE_PERMISSION
            )
        }
        
    }

    //////추후 본인 위치 찾으면 => MainActivity가 뜨게 수정. 현재는 touchEvent로 Main진입
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.getAction()) {
//            MotionEvent.ACTION_DOWN -> {} //화면 누르기 시작
//            MotionEvent.ACTION_MOVE -> {} //손가락 움직일 때
            MotionEvent.ACTION_UP -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            MotionEvent.ACTION_CANCEL -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else -> {}
        }
        return true
    }

    private fun allPermissionsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(bootCheckContext(), it) == PackageManager.PERMISSION_GRANTED
    }
}