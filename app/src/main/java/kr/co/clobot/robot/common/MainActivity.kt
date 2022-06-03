package kr.co.clobot.robot.common

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import kr.co.clobot.robot.common.databinding.ActivityMainBinding

class MainActivity : RobotActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private lateinit var mActivityMainBinding: ActivityMainBinding
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val REQUEST_EXTERNAL_STORAGE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding.root)

        verifyStoragePermissions(this)

        var homeBtn : Button = mActivityMainBinding.homeBtn
        var backBtn : Button = mActivityMainBinding.backBtn

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        backBtn.setOnClickListener{
            //navController.navigateUp(appBarConfiguration)
            navController.navigateUp()
        }

        homeBtn.setOnClickListener {
            //val navController = findNavController(R.id.nav_host_fragment_content_main)
            //navController.navigateUp()
            //startActivity(Intent(this, MainActivity::class.java))
            navController.navigate(R.id.main)
        }
    }

    private fun verifyStoragePermissions(activity: MainActivity) {
        val permission = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }

    }

    override fun onResume() {
        RobotPlatform.instacne.connect(this)
        var start: Intent = Intent(this, RobotEventService::class.java)
        this.startService(start)
        super.onResume()
    }

    override fun onPause() {
        var start: Intent = Intent(this, RobotEventService::class.java)
        this.stopService(start)
        super.onPause()
        Log.d(TAG, "API Demo pause")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}