package com.lge.support.second.application

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.Display
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.googlecloudmanager.GoogleTranslateV3
import com.example.googlecloudmanager.data.GoogleCloudApi
import com.example.googlecloudmanager.domain.GoogleCloudRepository
import com.lge.robot.platform.navigation.navigation.NavigationManager
import com.lge.support.second.application.main.data.chatbot.ChatbotApi
import com.lge.support.second.application.databinding.ActivityMainBinding
import com.lge.support.second.application.main.view.*
import com.lge.support.second.application.main.view.subView.SubScreen
import com.lge.support.second.application.main.model.MainViewModel
import com.lge.support.second.application.main.repository.ChatbotRepository
import com.lge.support.second.application.main.repository.PageConfigRepo
import com.lge.support.second.application.main.repository.RobotRepository
import com.lge.support.second.application.main.view.answer_1
import com.lge.support.second.application.main.view.template.*
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

        //////////////////chatbot 질의->화면 바꿔주기 위한 용도/////
        lateinit var page_id: String
        lateinit var tpl_id: String
        lateinit var r_status: String

        /////////////////chatbot으로 들어온 page인지 확인 용//////
        var chatPage: Boolean = false

        ////////////////chatbot제공 이미지 파일이 여러개인 경우->mutable 리스트 사용////
        var messageSize: Int = 0
        val BitmapArray = ArrayList<Bitmap>()

        ///////////////chatbot제공 이미지 한 개인 경우............................
        lateinit var url: String
        lateinit var urlDecoder: ByteArray
        lateinit var urlBitmap: Bitmap

        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private val PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO
        )
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val REQUEST_EXTERNAL_STORAGE = 1

        fun mainContext(): Context {
            return instance
        }
    }

    private lateinit var mActivityMainBinding: ActivityMainBinding
    private lateinit var language_code: String

    //display
    private lateinit var displayManager: DisplayManager
    private lateinit var displays: Array<Display>
    lateinit var subTest: SubScreen
    lateinit var head: HeadPresentation
    //lateinit var subTest2 : SubScreen2

    private lateinit var mNavigationManager: NavigationManager
    private val chatbotService = ChatbotApi.getInstance()
    private lateinit var googleService: GoogleCloudApi;
    //var qiBtn : ImageView = findViewById(R.id.qiMessage)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding.root)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_main, main()).commit()

        //toolbar
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        Log.i(tk_TAG, "MainActivity OnCreate")

//        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_main)
        val googleCredential: InputStream? = this.resources?.openRawResource(R.raw.credential)
        googleService = GoogleCloudApi.getInstance(googleCredential!!, UUID.randomUUID().toString())

        val googleTranslateCredential: InputStream? =
            this.resources?.openRawResource(R.raw.credential)
        if (googleTranslateCredential != null) {
            GoogleTranslateV3.initialize(
                googleTranslateCredential,
                UUID.randomUUID().toString(),
            )
        }

        ///////////////////지역 변수 선언 및 초기화////////////////
        var micBtn: Button = findViewById(R.id.micBtn)
        var homeBtn: Button = findViewById(R.id.homeBtn)
        var backBtn: Button = findViewById(R.id.backBtn)
        var korBtn: Button = findViewById(R.id.korBtn)
        var enBtn: Button = findViewById(R.id.enBtn)

        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        viewModel = ViewModelProvider(
            this@MainActivity,
            MainViewModel.Factory(
                ChatbotRepository(chatbotService),
                GoogleCloudRepository(googleService),
                RobotRepository(),
                PageConfigRepo()
            )
        ).get(MainViewModel::class.java)

        displays = displayManager.displays

        if (displays.isNotEmpty()) {
            subTest = SubScreen(this, displays[1])
            subTest.show()

            head = HeadPresentation(this, displays[2])
            head.show()
        }

        verifyStoragePermissions(this)
        ActivityCompat.requestPermissions(
            this, PERMISSIONS, REQUEST_RECORD_AUDIO_PERMISSION
        )

        val sharedPreferences = getSharedPreferences("Setting", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
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
//            viewModel.speechResponse()
//            val pois = mPoiManager.getAllPoi()
            viewModel.dockingRequest()
            subTest.findViewById<TextView>(R.id.sub_textView).setText("docking - start")
        }
        backBtn.setOnClickListener {
//            RobotEventService.unDocking().launchIn(lifecycleScope)
            supportFragmentManager.popBackStack()
            viewModel.stop()
        }
        homeBtn.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            viewModel.stop()
//            GoogleTTS.stop()
            //chatPage = false
        }
        korBtn.setOnClickListener {
            setLocate("ko")
            // TODO(Set Language to ko)
//            GoogleSTT.setLanguage(Language.Korean)
            recreate()
        }
        enBtn.setOnClickListener {
            setLocate("en")
            // TODO(Set Language to en)
//            GoogleSTT.setLanguage(Language.English)
            recreate()
        }

        findViewById<ImageView>(R.id.qiMessage).setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_main, chat())
                .addToBackStack(null).commit()

//            var testintent : Intent = Intent(this, popupActivity::class.java)
//            startActivity(testintent)
            var dialog = Dialog(this)
            dialog.setContentView(R.layout.count_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            var text : TextView = dialog.findViewById(R.id.dialog_testText)
            text.setText("3")

            /////////0.01초 뒤 부터 카운트 (3)
            Handler().postDelayed({
                dialog.show()
            }, 1000/100)
            ////////1초 보여주고 2
            Handler().postDelayed({
                dialog.hide()
                text.setText("2")
                dialog.show()
            }, 1000)
            //////1초 보여주고 1
            Handler().postDelayed({
                dialog.hide()
                text.setText("1")
                dialog.show()
            }, 2000)
            Handler().postDelayed({
                dialog.hide()
            }, 2900) ///////////2.9sec
        }
        ////////////main-Button Listener end////////////

        // Robot Service Observer
//        RobotEventService.mActionStatus.observe(this) { event ->
//            Log.d("MainActivity", "$event")
//        }
        // ViewModel Observe TODO(Chatbot Request Base Action)
        viewModel.queryResult.observe(this) {
            Log.d("ViewModel", "chatbot data change, $it")
            if (it == null) {
                return@observe
            }
            viewModel.speak(this, it.data.result.fulfillment.speech[0])
            head.changeText(it.data.result.fulfillment.speech[0] + " (" + it.data.result.fulfillment.custom_code.head + ")")
//            it.data.result.fulfillment.messages.forEach { message ->
//                Log.d("ViewModel Observe", message.image.toString())
//            }
            page_id = it.data.result.fulfillment.custom_code.page_id
            tpl_id = it.data.result.fulfillment.template_id
            r_status = it.data.result.fulfillment.response_status

            ///////test --------------- check
            Log.d("tk_test", "page_id is : " + page_id)
            Log.d("tk_test", "template_id is : " + tpl_id)
            Log.d("tk_test", "response_status is : " + r_status)

            ////////////////url 없는 경우 error => 있을 때만 받아옴/////////
            if (!it.data.result.fulfillment.messages.isNullOrEmpty()) {
                if (!it.data.result.fulfillment.messages[0].image.isNullOrEmpty()) {
                    messageSize = it.data.result.fulfillment.messages.size
                    Log.d("tk_test", "message list size is " + messageSize)

                    for (i in 0..messageSize - 1) {
                        url = it.data.result.fulfillment.messages[0].image[i].url.substring(
                            it.data.result.fulfillment.messages[0].image[0].url.indexOf(",") + 1
                        )
                        urlDecoder = Base64.decode(url, Base64.DEFAULT)

                        Log.d("tk_test", "urlDecode is " + urlDecoder)

                        urlBitmap = BitmapFactory.decodeByteArray(urlDecoder, 0, urlDecoder.size)
                        BitmapArray.add(urlBitmap)
                        Log.d("tk_test", "bitmap array is " + BitmapArray.get(i))
                    }

                    if (BitmapArray.size > messageSize) {
                        for (i in messageSize..BitmapArray.size - 1) {
                            BitmapArray.removeAt(i)
                        }
                    }
                }
            }
            ///////////////////////chatbot 제공 이미지 정보 저장 끝///////////////////

            ///////////////////chatbot질의 페이지에서만 page바꿔줌. 나머지는 발화만
            if (chatPage == true) {
                if (page_id != "") { //////////////page id가 존재
                    if (tpl_id == "") {///////////tpl_id는 없음
                        if (r_status == "match") ///tpl_id가 공백일 경우에는 response_status값이 match -> page_id
                            changeFragment(page_id)
                    }
                    else //////tpl_id가 있음
                        changeFragment(page_id)
                } else { /////////page id가 없음
                    if (tpl_id != "") { /////////template id는 있음
                        changeFragment(tpl_id)
                    }
                }
            }
        }
        viewModel.getResponse("intro", "intro")
    } //onCreate

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            android.R.id.home -> finish()
//        }
//        return super.onOptionsItemSelected(item)
//    }

    ///////////////////////////////////////language
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

    //fragment change
    fun changeFragment(page_id: String) {
        when (page_id) {
            "play" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, play())
                    .addToBackStack(null).commit()
            }

            "theater" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater())
                    .addToBackStack(null).commit()
            }

            "enjoy" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, enjoy())
                    .addToBackStack(null).commit()
            }

            "theater-map" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater_map())
                    .addToBackStack(null).commit()
            }

            "theater-parking" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, theater_parking_bus())
                    .addToBackStack(null).commit()
            }

//            "enjoy-exhbn" -> {
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, enjoy_1())
//                    .addToBackStack(null).commit()
//            }

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

            "answer_4", "tpl-com-05" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_4())
                    .addToBackStack(null).commit()
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
        super.onResume()
        Log.d(TAG, "API Demo resume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "API Demo pause")
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
        val id = item.itemId

        if (id == R.id.action_exit) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        /////////////////////test Activity///////////////////////////
        else if (id == R.id.testBtn) {
            val testIntent = Intent(this, TestActivity::class.java)
            startActivity(testIntent)
        } else if (id == R.id.enginBtn) {
//            var settingintent : Intent = Intent(this, TestActivity::class.java)
//            this.startActivity(settingintent)
            var intent = packageManager.getLaunchIntentForPackage("com.lge.engineermenu")
            //intent?.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            var options = ActivityOptions.makeBasic()
            options.launchDisplayId = 1

            subTest.hide()
            startActivity(intent, options.toBundle())
            //startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}