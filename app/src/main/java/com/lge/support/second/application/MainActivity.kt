package com.lge.support.second.application

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.view.Display
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.googlecloudmanager.common.Language
import com.example.googlecloudmanager.data.GoogleCloudApi
import com.example.googlecloudmanager.domain.GoogleCloudRepository
import com.lge.robot.platform.data.NaviStatus2
import com.lge.support.second.application.data.chatbot.ChatbotApi
import com.lge.support.second.application.data.robot.MoveState
import com.lge.support.second.application.data.robot.RobotState
import com.lge.support.second.application.databinding.ActivityMainBinding
import com.lge.support.second.application.managers.mqtt.MessageConnector
import com.lge.support.second.application.model.MainViewModel
import com.lge.support.second.application.model.RobotViewModel
import com.lge.support.second.application.repository.SceneConfigRepo
import com.lge.support.second.application.util.LEDState
import com.lge.support.second.application.view.*
import com.lge.support.second.application.view.adapter.currentBackScreen
import com.lge.support.second.application.view.adapter.hideBackScreen
import com.lge.support.second.application.view.adapter.showBackScreen
import com.lge.support.second.application.view.chatView.chat
import com.lge.support.second.application.view.chatView.chat_fail
import com.lge.support.second.application.view.docent.*
import com.lge.support.second.application.view.subView.*
import com.lge.support.second.application.view.template.*
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    init {
        instance = this
    }

    //for viewmodel test log_Tag
    companion object {
        lateinit var instance: MainActivity
        const val tk_TAG: String = "logForTest"
        //lateinit var tkTestViewModel: TkTestViewModel

        lateinit var viewModel: MainViewModel
        lateinit var robotViewModel: RobotViewModel

        //////////////////chatbot ??????->?????? ???????????? ?????? ??????/////
        lateinit var page_id: String
        lateinit var tpl_id: String
        lateinit var r_status: String

        ///////////////?????? ?????? ?????? ??????/////////////////////
        var notMachCnt = 0

        /////////////////chatbot?????? ????????? page?????? ?????? ???//////
        var chatPage: Boolean = false

        ////////////////chatbot?????? ????????? ????????? ???????????? ??????->mutable ????????? ??????////
        var messageSize: Int = 0
        var imgBtnSize: Int = 0
        val urlArray = ArrayList<String>() //message??? ????????? image
        var urlArray2 = ArrayList<String>()
        val BitmapArray = ArrayList<Bitmap>()

        ///////////////chatbot?????? ????????? ?????? ??? 2???
        lateinit var url: String ///???????????? ??? url
        lateinit var urlDecoder: ByteArray //???????????? ????????? ?????? ??????
        lateinit var urlBitmap: Bitmap
        lateinit var uri: Uri //uri???????????? ?????? ?????? ????????? ??????

        /////////
        lateinit var inStr: String
        lateinit var speechStr: String
        var descriptStr: String = ""

        lateinit var information_back: information_back
        lateinit var subVideo: back_video
        lateinit var standby: standby
        lateinit var movement_normal: movement_normal
        lateinit var promote_normal: promote_normal
        lateinit var docent_back: docent_back
        lateinit var move_docent: moveDocent
        lateinit var emergency_back: emergency_screen
        lateinit var emergency_front: emergency_screen
        lateinit var docking_screen: docking
        lateinit var undocking_screen: undocking
        lateinit var charging_screen : charging

        fun mainContext(): Context {
            return instance
        }

        // MQTT
        val mqttMgr by lazy { MessageConnector.getInstance() }
    }

    private lateinit var mActivityMainBinding: ActivityMainBinding
    private lateinit var language_code: String

    //display
    private lateinit var displayManager: DisplayManager
    private lateinit var displays: Array<Display>
    lateinit var head: HeadPresentation

    private lateinit var googleService: GoogleCloudApi;

    ////////////////////????????? ????????? ????????? ?????? ????????? ??????/////////////
    var lastClickTime: Long = 0 // ????????? ?????? ??????
    var clickTime = 0 // ?????? ??? ??????
    val TIMES_REQUIRED = 2 // ??? ????????? ?????? ?????? val TIMES_REQUIRED = 6
    val TIME_TIMEOUT = 2000 //?????? ?????? ?????? ?????? ????????? 2???..

    lateinit var korBtn: Button
    lateinit var enBtn: Button
    lateinit var jpnBtn: Button
    lateinit var chnBtn: Button
    /////////////////????????????/////////////

    ///////////////////////////////////////////////////////on Create ////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding.root)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_main, main()).commit()

        //toolbar
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        Log.i(tk_TAG, "MainActivity OnCreate")

//        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_main)
        val googleCredential: InputStream? = this.resources?.openRawResource(R.raw.credential)
        googleService = GoogleCloudApi.getInstance(googleCredential!!, UUID.randomUUID().toString())

        ///////////////////?????? ?????? ?????? ??? ?????????////////////////
        val micBtn: Button = findViewById(R.id.micBtn)
        val homeBtn: Button = findViewById(R.id.homeBtn)
        val backBtn: Button = findViewById(R.id.backBtn)
        korBtn = findViewById(R.id.korBtn)
        enBtn = findViewById(R.id.enBtn)
        val adminBtn: Button = findViewById(R.id.EnterAdminBtn)
        jpnBtn = findViewById(R.id.jpnBtn)
        chnBtn = findViewById(R.id.chiBtn)

        val customDialog: Dialog = CustomProgressDialogue(this)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        viewModel = ViewModelProvider(
            this@MainActivity,
            MainViewModel.Factory(
                MainApplication.mChatbotRepo,
                GoogleCloudRepository(googleService),
                MainApplication.mPageInfoRepo,
                application as MainApplication
            )
        ).get(MainViewModel::class.java)

        robotViewModel = ViewModelProvider(
            this@MainActivity,
            RobotViewModel.Factory(
                MainApplication.mRobotRepo,
                application as MainApplication
            )
        ).get(RobotViewModel::class.java)

        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displays = displayManager.displays

        if (displays.isNotEmpty()) {
            information_back = information_back(this, displays[1])
            information_back.show()
            currentBackScreen = "information_back"

            standby = standby(this, displays[0])

            subVideo = back_video(this, displays[1])

            head = HeadPresentation(this, displays[2])
            head.show()

            ////////////////////??????
            move_docent = moveDocent(this, displays[1])
            movement_normal = movement_normal(this, displays[0])
            promote_normal = promote_normal(this, displays[1])
            docent_back = docent_back(this, displays[1])
            emergency_back = emergency_screen(this, displays[1])
            emergency_front = emergency_screen(this, displays[0])
            docking_screen = docking(this, displays[1])
            undocking_screen = undocking(this, displays[1])
            charging_screen = charging(this, displays[1])
        }

        // CHECK PERMISSIONS
//        if (allPermissionsGranted()) {
//            //standby.show()
//            Log.d(TAG, "all granted")
//        } else {
//            ActivityCompat.requestPermissions(
//                this, PERMISSIONS, REQUEST_CODE_PERMISSION
//            )
//        }

        // MQTT Service start
        //mqttMgr.initFunc()

        //?????? ????????? ????????? ???????????? ??? ????????????
        val locale: Locale = applicationContext.resources.configuration.locale
        Log.i(tk_TAG, "???????????? : " + locale.getCountry() + " ?????? ?????? : " + locale.language)

        val sharedPreferences = getSharedPreferences("Setting", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", locale.language)

        when (val currentLang = boot_check.langPref.getString("My_Lang", locale.language)) {
            "ko" -> {
                viewModel.setLanguage(Language.Korean)
                setLocate(currentLang.toString())
            }
            "en" -> {
                setLocate("en")
                viewModel.setLanguage(Language.English)
            }
            "zh" -> {
                setLocate("zh")
                viewModel.setLanguage(Language.Chinese)
            }
            "ja" -> {
                setLocate("ja")
                viewModel.setLanguage(Language.Japanese)
            }
        }

        if (language != null) {
            Log.i(tk_TAG, "language : " + language)
            language_code = language
        }

        ////////////main-Button Listener////////////
        micBtn.setOnClickListener {
//            tkTestViewModel.updateValue(actionType = ActionType.Test, 1)
//            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_main, chat())
//                .addToBackStack(null).commit()
//
            viewModel.enrollSchedule()
            head.changeExpression(Expression.CURIOUS)
        }
        backBtn.setOnClickListener {
            supportFragmentManager.popBackStack()
//            viewModel.ttsStop()
        }
        homeBtn.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            viewModel.resetCurrentPage()
//            viewModel.ttsStop()
            //chatPage = false
        }
        korBtn.setOnClickListener {
            setLocate("ko")
            viewModel.setLanguage(Language.Korean)
            setLanguageColor("ko")
            recreate()
        }
        enBtn.setOnClickListener {
            setLocate("en")
            viewModel.setLanguage(Language.English)
            setLanguageColor("en")
            recreate()
        }
        chnBtn.setOnClickListener {
            setLocate("zh")
            viewModel.setLanguage(Language.Chinese)
            setLanguageColor("zh")
            recreate()
        }
        jpnBtn.setOnClickListener {
            setLocate("ja")
            viewModel.setLanguage(Language.Japanese)
            setLanguageColor("ja")
            recreate()
        }
        adminBtn.setOnClickListener { v ->
            TouchContinously()
        }

        ////////////main-Button Listener end///////////
        // Robot Service Observer
        robotViewModel.robotState.observe(this) { state ->
            Log.d(TAG, "$state, ROBOT STATE CHANGED")
            when (state) {
                RobotState.EMERGENCY -> {
                    Log.e(TAG, "EMERGENCY DETECT ON MainActivity")
                    head.changeExpression(Expression.CURIOUS)
                    showBackScreen("emergency")
                    robotViewModel.setLed(LEDState.EMERGENCY)
                }
                RobotState.EMERGENCY_RELEASE -> {
                    Log.e(TAG, "EMERGENCY RELEASE DETECT ON MainActivity")
                }
                RobotState.INIT -> {
                    println("robot INITIALIZED")
                    head.changeExpression(Expression.WINK)
                }
                RobotState.CHARGING -> {

                }
            }
        }

        viewModel.queryResult.observe(this) {
            Log.d(TAG, "chatbot data changed, $it")
            if (it == null) {
                return@observe
            }

            page_id = it.customCode.page_id
            tpl_id = it.template_id

            var speechSize = it.speech.size
            Log.d("tk_test", "speechSize is " + speechSize)

            speechStr = ""
            for (i in 0 until (speechSize)) {
                speechStr += it.speech[i]
                Log.d("tk_test", "speech string is " + speechStr)
            }

            if (speechStr != "")
                viewModel.ttsSpeak(applicationContext, speechStr)

            if (it.template_id != null) {
                tpl_id = it.template_id
            } else tpl_id = ""
            r_status = it.response_status
            inStr = it.in_str

            /////////////?????? ?????? ?????? ??????/////////
            if (r_status == "not_match") {
                notMachCnt += 1
                if (notMachCnt == 3) { /////?????? ??? ???
                    notMachCnt = 0 /////?????? ????????? ??????????????? ?????? ?????? ?????? ?????? ??????.
                    changeFragment("chat-fail") ///?????? ???????????? ??????
                }
            }

            ///////test --------------- check
            Log.d("tk_test", "page_id is : " + page_id)
            Log.d("tk_test", "template_id is : " + tpl_id)
            Log.d("tk_test", "response_status is : " + r_status)
            Log.d("tk_test", "in string is " + inStr)

            ////////////////url ?????? ?????? error => ?????? ?????? ?????????/////////
            if (!it.messages.isNullOrEmpty()) {
                if (!it.messages[0].image.isNullOrEmpty()) {
                    messageSize = it.messages.size
                    Log.d("tk_test", "message list size is " + messageSize)

                    if (it.messages[0].image[0].url.substring(0 until 5) == "https") {
                        urlArray.clear()
                        BitmapArray.clear()

                        Log.i(
                            "tk_test",
                            "img 1 : " + it.messages[0].image[0].url
                        )
                        Log.i(
                            "tk_test",
                            "img 2 : " + it.messages[0].image[1].url
                        )

                        for (i in 0..messageSize) {
                            url = it.messages[0].image[i].url
                            Log.d("tk_test", "img type is uri " + url) //?????? ??? ??????????????? ??????

                            urlArray.add(url) ///////string?????? ???????????????.
                            Log.d("tk_test", "urlArray add done " + urlArray[i])
                        }
                    } else { ///////////////http??? ???????????? ?????????
                        urlArray.clear()
                        BitmapArray.clear()

                        for (i in 0 until messageSize) {
                            url = it.messages[0].image[i].url.substring(
                                it.messages[0].image[0].url.indexOf(",") + 1
                            )

                            Log.d("tk_test", "url is " + url)
                            urlDecoder = Base64.decode(url, Base64.DEFAULT)

                            Log.d("tk_test", "urlDecode is " + urlDecoder)

                            urlBitmap =
                                BitmapFactory.decodeByteArray(urlDecoder, 0, urlDecoder.size)
                            BitmapArray.add(urlBitmap)
                            Log.d("tk_test", "bitmap array is " + BitmapArray.get(i))
                        }
                    }
                }

                if (!it.messages[0].imageButton.isNullOrEmpty()) {
                    imgBtnSize = it.messages[0].imageButton.size
                    Log.d("tk_test", "message Button list size is " + imgBtnSize)

                    urlArray2.clear()

                    for (i in 0..imgBtnSize - 1) {
                        url = it.messages[0].imageButton[i].url
                        Log.d("tk_test", "imageButton url is " + url) //?????? ??? ??????????????? ??????

                        urlArray2.add(url) ///////string?????? ???????????????.
                        Log.d("tk_test", "urlArray2 add done " + urlArray2[i])
                    }
                }
            }
            ///////////////////////chatbot ?????? ????????? ?????? ?????? ???///////////////////

            ///////////////////chatbot?????? ?????????????????? page?????????. ???????????? ?????????
            if (chatPage) {
                if (page_id != "") { //////////////page id??? ??????
                    if (tpl_id == "") {///////////tpl_id??? ??????
                        if (r_status == "match") ///tpl_id??? ????????? ???????????? response_status?????? match -> page_id
                        {
                            customDialog.show()
                            changeFragment(page_id)
                            customDialog.dismiss()
                        }
                    } else //////tpl_id??? ??????
                    {
                        customDialog.show()
                        changeFragment(tpl_id)
                        customDialog.dismiss()
                    }
                } else { /////////page id??? ??????
                    if (tpl_id != "") { /////////template id??? ??????
                        customDialog.show()
                        changeFragment(tpl_id)
                        customDialog.dismiss()
                    }
                }
            }
        } // observe queryResult
        viewModel.currentPage.observe(this) {
            if (it == null || chatPage || it == "") return@observe
            println("currentPage: $it")
            customDialog.show()
            changeFragment(it)
            customDialog.hide()
        }

        robotViewModel.moveState.observe(this) {
            when (it) {
                MoveState.MOVE_START -> movement_normal.show()
                MoveState.DOCENT_MOVE -> showBackScreen("move_docent")
                MoveState.STAY -> Toast.makeText(this, "STAY", Toast.LENGTH_SHORT)
                MoveState.MOVE_DONE -> movement_normal.hide()
            }
        }
    } //onCreate

    private fun setLanguageColor(currentLang: String?) {
        korBtn.setTextColor(getColor(R.color.gonju_disabled))
        enBtn.setTextColor(getColor(R.color.gonju_disabled))
        chnBtn.setTextColor(getColor(R.color.gonju_disabled))
        jpnBtn.setTextColor(getColor(R.color.gonju_disabled))

        when (currentLang) {
            "ko" -> korBtn.setTextColor(getColor(R.color.gongju_title)) //selected
            "en" -> enBtn.setTextColor(getColor(R.color.gongju_title))
            "zh" -> chnBtn.setTextColor(getColor(R.color.gongju_title))
            "ja" -> jpnBtn.setTextColor(getColor(R.color.gongju_title))
        }
    }

    private fun TouchContinously() { //////////?????? ?????? ?????? ?????? ??? ???????????? ??????
        if (SystemClock.elapsedRealtime() - lastClickTime < TIME_TIMEOUT) { //???????????? ?????? x
            clickTime++ //?????? ?????? ??????
        } else {
            clickTime = 1; //???????????? ??????? -> ????????? ?????? ?????????
        }
        lastClickTime = SystemClock.elapsedRealtime();

        if (clickTime == TIMES_REQUIRED) {
            // TODO ?????? ?????? ?????? ???
            startActivity(Intent(this, AdminPage::class.java)) //????????? ????????? ??????
        }
    }

    ///////////////////////////////////////language
    private fun setLocate(Lang: String) {
        Log.d(tk_TAG, "setLocate")
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        boot_check.editor.putString("My_Lang", Lang)
        boot_check.editor.apply()
        setLanguageColor(Lang)
    }

    //fragment change
    fun changeFragment(page_id: String) {
        viewModel.updatePageInfo(page_id).observe(this) {
            Log.d("hjbae", "pageInfo : $it")
        }
        when (page_id) {
            "information" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, information())
                    .addToBackStack(null).commit()
            }

            "exhibits-ungjin", "arts" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, exhibits())
                    .addToBackStack(null).commit()
            }

            "location-facility" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, location())
                    .addToBackStack(null).commit()
            }

            "docent-select" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, docent_select())
                    .addToBackStack(null).commit()
            }

            "theater-map" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater_map())
                    .addToBackStack(null).commit()
            }

            /////////////////////////////chat bot/////////////////////////////
            "answer_1", "tpl-com-00" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_1())
                    .addToBackStack(null).commit()
            }

            "answer_2", "tpl-com-01" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_2())
                    .addToBackStack(null).commit()
            }

            "answer-list_1", "tpl-com-02" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, answer_list_1())
                    .addToBackStack(null).commit()
            }

            "answer_3", "tpl-com-03" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_3())
                    .addToBackStack(null).commit()
            }

            "answer-list_2", "tpl-com-04" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, answer_list_2())
                    .addToBackStack(null).commit()
            }

            // TEST FOR MMCA SCENARIO
            "tpl-single-art", "answer-art" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, answer_3())
                    .addToBackStack(null).commit()
            }

            "answer_4", "tpl-com-05" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_4())
                    .addToBackStack(null).commit()
            }
            "docent" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, move_docent())
                    .addToBackStack(null).commit()
            }

            "docent-play" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, move_docent())
                    .addToBackStack(null).commit()
            }

            "chat-fail" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, chat_fail())
                    .addToBackStack(null).commit()
            }
            "test-docent" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, test_docent())
                    .addToBackStack(null).commit()
            }
            "chat" -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, chat())
                    .addToBackStack(null).commit()
            }

            "answer-location","answer-exhibits" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, answer_location()) //answer_location
                    .addToBackStack(null).commit()
            }

            "move-arrive_1" -> {
//                moveArrive1.show()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, move_arrive1())
                    .addToBackStack(null).commit()
            }
            "move-arrive_2" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, move_arrive2())
                    .addToBackStack(null).commit()
            }
        }
    }

//    private fun allPermissionsGranted() = PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(mainContext(), it) == PackageManager.PERMISSION_GRANTED
//    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "API Demo resume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "API Demo pause")
    }

    override fun onStart() {
        super.onStart()
        RobotPlatform.connect(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tk_TAG, "Main Destroy")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_exit -> {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
            R.id.testBtn -> {
                val testIntent = Intent(this, TestActivity::class.java)
                startActivity(testIntent)
            }
            R.id.enginBtn -> {
                var intent = packageManager.getLaunchIntentForPackage("com.lge.engineermenu")
                //intent?.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

                var options = ActivityOptions.makeBasic()
                options.launchDisplayId = 1

                information_back.hide()
                docent_back.hide()
                startActivity(intent, options.toBundle())
            }
            R.id.webBtn -> {
                startActivity(Intent(this, webViewTestActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }
}