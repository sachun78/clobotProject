package com.lge.support.second.application

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lge.support.second.application.main.model.ActionType
import com.lge.support.second.application.main.model.TkTestViewModel
import com.lge.support.second.application.main.view.*
import com.lge.support.second.application.main.view.subView.subScreen
import kr.co.clobot.robot.common.R
import kr.co.clobot.robot.common.databinding.ActivityMainBinding
import java.util.*

class MainActivity : RobotActivity() {
    private val TAG = MainActivity::class.java.simpleName

    //for viewmodel test log_Tag
    companion object{
        const val tk_TAG: String = "logForTest"
        lateinit var tkTestViewModel: TkTestViewModel
    }

    private lateinit var mActivityMainBinding: ActivityMainBinding

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val REQUEST_EXTERNAL_STORAGE = 1

    //언어 코드 바꾸기 용 변수 설정
    private lateinit var language_code : String

    //display용
    private lateinit var displayManager: DisplayManager //디스플레이 매니저가 할당 될 변수
    private lateinit var displays: Array<Display>       //디스플레이 목록이 담길 배열
    lateinit var subTest : subScreen

    //for viewmodel test (activity change test)
    //lateinit(나중에 값이 설정)
    //lateinit var tkTestViewModel: TkTestViewModel
    //private val tkTestViewModel: TkTestViewModel by viewModels()

    //visivility 테스트
    //var qiBtn : ImageView = findViewById(R.id.qiMessage)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding.root)
        //기본 화면 세팅(mainActivity가 gnb -> 그 위에 main)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_main, main()).commit()
        
        //toolbar세팅
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        
        Log.i(tk_TAG, "MainActivity OnCreate")

        ////////////지역 변수 목록///////////
//        var qiBtn : ImageView = findViewById(R.id.qiMessage)
//        qiBtn.visibility = View.INVISIBLE
//        var qiView : View = findViewById(R.id.test_toolbar)
//        var qiVisibility : ImageView = qiView.findViewById(R.id.qiMessage)
        var fragment : Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_main)


        ////////////사용 변수 초기화////////////
        var micBtn : Button = findViewById(R.id.micBtn)
        var homeBtn : Button = findViewById(R.id.homeBtn)
        var backBtn : Button = findViewById(R.id.backBtn)
        var korBtn : Button = findViewById(R.id.korBtn)
        var enBtn : Button = findViewById(R.id.enBtn)
        //var qiInfoImg : ImageView = findViewById(R.id.qiMessage)
        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager//디스플레이 매니저 할당


        if(displayManager != null) {
            //디스플레이매니저를 통해 디스플레이 리스트 가져오기
            displays = displayManager.displays

            if (displays.size > 0) {
                subTest = subScreen(this, displays[1]) // 프레젠테이션 생성 및 어떤 디스플레이에 보여줄지 설정
            }
        }

        /////////////////////////////////for viewmodel test/////////////////////////////////
        tkTestViewModel = ViewModelProvider(this).get(TkTestViewModel::class.java) //뷰모델 객체 생성
//        참고 private val viewModel: NavigationModel by viewModels()

        tkTestViewModel.currentValue.observe(this, Observer{
            Log.i(tk_TAG, "MainActivity - tkTestViewModel - currentValue live Data 변경 : $it")
            if(it.toInt() %2 == 1){
                subTest.show()
            }
            else{
                subTest.hide()
            }
        })
        /////////////////////////////////for viewmodel test end /////////////////////////////////
        verifyStoragePermissions(this)

        //저장된 언어 코드 불러오기
        val sharedPreferences = getSharedPreferences("Setting", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if(language != null) {
            Log.i(tk_TAG, "language : " + language)
            language_code = language
        }


        ////////////main-Button Listener////////////
        micBtn.setOnClickListener {
            tkTestViewModel.updateValue(actionType = ActionType.Test, 1)
        }
        backBtn.setOnClickListener{
            supportFragmentManager.popBackStack()
        }
        homeBtn.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        //저장된 언어코드 - 버튼
        korBtn.setOnClickListener {
            setLocate("ko")
            recreate() //Activity 재생성 (새로고침 다른 방법은 안 되나...?)
            //supportFragmentManager.beginTransaction().detach().attach().commit()
        }
        enBtn.setOnClickListener {
            setLocate("en")
            recreate()
        }

        findViewById<ImageView>(R.id.qiMessage).setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_main, chat()).addToBackStack(null).commit()
        }

        ////////////main-Button Listener end////////////

    } //onCreate

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            android.R.id.home -> finish()
//        }
//        return super.onOptionsItemSelected(item)
//    }

    ///////////////////////////////////////language설정
    private fun setLocate(Lang: String) {
        Log.d(tk_TAG, "setLocate")
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences(tk_TAG, Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    //fragment간 이동
    fun changeFragment(index : Int){ //string으로 바꿀 거 생각
        when(index) {
            1 -> { //국립극장 공연보기
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, play()).addToBackStack(null).commit()
            }

            2 -> { //국립극장 둘러보기
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater()).addToBackStack(null).commit()

            }

            3 -> { //국립국장 즐기기
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, enjoy())
                    .addToBackStack(null).commit()
            }

            21 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main,
                    theater_map()
                ).addToBackStack(null).commit()
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater_map()).commit()
            }

            22 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater_parking_bus())
                    .addToBackStack(null).commit()
            }

            51 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_1()).addToBackStack(null).commit()
            }
        }
    }

//    fun changeVisibility(index : Int) {
//        when(index) {
//            0 -> {
//                qiVisibility.visibility = View.GONE
//            }
//
//            1 -> {
//                qiVisibility.visibility = View.VISIBLE
//            }
//        }
//    }

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
        Log.i(tk_TAG, "Main Destroy")
    }


}

