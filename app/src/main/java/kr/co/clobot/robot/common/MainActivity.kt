package kr.co.clobot.robot.common

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import io.reactivex.android.plugins.RxAndroidPlugins.reset
import io.reactivex.plugins.RxJavaPlugins.reset
import kr.co.clobot.robot.common.databinding.ActivityMainBinding
import kr.co.clobot.robot.common.main.view.enjoy
import kr.co.clobot.robot.common.main.view.main
import kr.co.clobot.robot.common.main.view.theater
import kr.co.clobot.robot.common.main.view.theater_map

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

        //기본 화면 세팅(mainActivity가 gnb -> 그 위에 main)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_main, main()).commit()

//        backBtn.setOnClickListener{
//            //navController.navigateUp(appBarConfiguration)
//            navController.navigateUp()
//        }
//
        homeBtn.setOnClickListener {
            //val navController = findNavController(R.id.nav_host_fragment_content_main)
            //navController.navigateUp()
            //startActivity(Intent(this, MainActivity::class.java))
            //navController.navigate(R.id.main)
            //navController.backStack
            //navController.popBackStack()
            //navController.navigate(R.id.main)
//            val navHostFragment : Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
//            navHostFragment?.childFragmentManager?.getFragments()?.get(0)
//
//            if (navHostFragment != null) {
//                navController.popBackStack(navHostFragment.id, true)
//            }
//            navController.navigate(R.id.main)
//            navigation.reset({})
            supportFragmentManager.beginTransaction().replace(R.id.fragment_main, main()).commit()
        }
    }


    //fragment간 이동
    fun changeFragment(index : Int){ //string으로 바꿀 거 생각
        when(index) {
            1 -> { //국립극장 공연보기
                    //supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater_map()).commit()
            }

            2 -> { //국립극장 둘러보기
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater()).commit()
            }

            3 -> { //국립국장 즐기기
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, enjoy()).commit()
            }

            31 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater_map()).commit()
            }
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