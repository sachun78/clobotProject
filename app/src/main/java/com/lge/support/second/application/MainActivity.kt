package com.lge.support.second.application

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Display
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.googlecloudmanager.GoogleTranslateV3
import com.example.googlecloudmanager.common.Language
import com.example.googlecloudmanager.data.GoogleCloudApi
import com.example.googlecloudmanager.domain.GoogleCloudRepository
import com.lge.robot.platform.data.PosXYZDeg
import com.lge.robot.platform.navigation.navigation.NavigationManager
import com.lge.robot.platform.power.PowerManager
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.main.data.chatbot.ChatbotApi
import com.lge.support.second.application.databinding.ActivityMainBinding
import com.lge.support.second.application.main.model.TkTestViewModel
import com.lge.support.second.application.main.view.*
import com.lge.support.second.application.main.view.subView.SubScreen
import com.lge.support.second.application.main.model.ChatbotViewModel
import com.lge.support.second.application.main.poi.PoiDbManager
import com.lge.support.second.application.main.repository.ChatbotRepository
import com.lge.support.second.application.main.view.answer_1
import com.lge.support.second.application.main.view.template.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*

class MainActivity : RobotActivity() {
    private val TAG = MainActivity::class.java.simpleName

    //for viewmodel test log_Tag
    companion object {
        const val tk_TAG: String = "logForTest"
        //lateinit var tkTestViewModel: TkTestViewModel

        lateinit var viewModel: ChatbotViewModel

        //////////////////chatbot 질의->화면 바꿔주기 위한 용도/////
        lateinit var page_id: String
        lateinit var tpl_id: String
        lateinit var r_status: String

        /////////////////chatbot으로 들어온 page인지 확인 용//////
        var chatPage : Boolean = false

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
    }

    private lateinit var mActivityMainBinding: ActivityMainBinding
    private lateinit var language_code: String

    //display
    private lateinit var displayManager: DisplayManager
    private lateinit var displays: Array<Display>
    lateinit var subTest: SubScreen
    lateinit var head: HeadPresentation
    //lateinit var subTest2 : SubScreen2

    //위에 전역 변수로 뺌
//    private val viewModel: ChatbotViewModel by lazy {
//        ViewModelProvider(
//            this@MainActivity,
//            ChatbotViewModel.Factory(ChatbotRepository(chatbotService))
//        ).get(ChatbotViewModel::class.java)
//    }
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

//        val googleSttCredential: InputStream? = this.resources?.openRawResource(R.raw.credential)
//        if (googleSttCredential != null) {
//            GoogleSTT.initialize(
//                googleSttCredential,
//                UUID.randomUUID().toString(),
//            )
//        }

        val googleTranslateCredential: InputStream? =
            this.resources?.openRawResource(R.raw.credential)
        if (googleTranslateCredential != null) {
            GoogleTranslateV3.initialize(
                googleTranslateCredential,
                UUID.randomUUID().toString(),
            )
        }

        var micBtn: Button = findViewById(R.id.micBtn)
        var homeBtn: Button = findViewById(R.id.homeBtn)
        var backBtn: Button = findViewById(R.id.backBtn)
        var korBtn: Button = findViewById(R.id.korBtn)
        var enBtn: Button = findViewById(R.id.enBtn)
        //var qiInfoImg : ImageView = findViewById(R.id.qiMessage)
        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        viewModel = ViewModelProvider(
            this@MainActivity,
            ChatbotViewModel.Factory(
                ChatbotRepository(chatbotService),
                GoogleCloudRepository(googleService)
            )
        ).get(ChatbotViewModel::class.java)

        displays = displayManager.displays

        if (displays.isNotEmpty()) {
            subTest = SubScreen(this, displays[1])
            subTest.show()

            head = HeadPresentation(this, displays[2])
            head.show()
        }

        /////////////////////////////////viewmodel test/////////////////////////////////
//        tkTestViewModel = ViewModelProvider(this).get(TkTestViewModel::class.java)
//        private val viewModel: NavigationModel by viewModels()

//        tkTestViewModel.currentValue.observe(this, Observer{
//            Log.i(tk_TAG, "MainActivity - tkTestViewModel - currentValue live Data change : $it")
//
//            if(it.toInt() %3 == 0){
//                subTest.show()
//            }
//            else if(it.toInt() %3 == 1){
//                subTest.show()
//            }
//            else if (it.toInt() %3 == 2){
//                subTest.hide()
//            }
//        })
        /////////////////////////////////viewmodel test end /////////////////////////////////

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
//            println(pois)
//            mNavigationManager.doUndockingEx()
//            mPoiManager.getInitPosition()?.let { it1 -> moveWithPoi(it1) }
//            val random = Random()
//            pois?.get(random.nextInt(7))?.let { it1 -> moveWithPoi(it1) }
//            moveWithPoi(mPoiManager.test())

            RobotEventService.docking().launchIn(lifecycleScope)
        }
        backBtn.setOnClickListener {
//            RobotEventService.unDocking().launchIn(lifecycleScope)
            supportFragmentManager.popBackStack()
//            GoogleTTS.stop()
        }
        homeBtn.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//            GoogleTTS.stop()
            //chatPage = false
        }
        korBtn.setOnClickListener {
            setLocate("ko")
            // TODO(Set Language to ko)
//            GoogleSTT.setLanguage(Language.Korean)
            recreate()
            //supportFragmentManager.beginTransaction().detach().attach().commit()
        }
        enBtn.setOnClickListener {
            setLocate("en")
            // TODO(Set Language to en)
//            GoogleSTT.setLanguage(Language.English)
            recreate()
            //refreshFragment(this, supportFragmentManager)
        }

        findViewById<ImageView>(R.id.qiMessage).setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_main, chat())
                .addToBackStack(null).commit()
        }
        ////////////main-Button Listener end////////////

        // Robot Service Observer
        RobotEventService.mActionStatus.observe(this) { event ->
            Log.d("MainActivity", "$event")
        }
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
            if(chatPage == true){
                if (page_id != "") { //////////////page id가 존재
                    if (tpl_id == "") {///////////tpl_id는 없음
                        if (r_status == "match") ///tpl_id가 공백일 경우에는 response_status값이 match -> page_id
                            changeFragment(page_id)
                    } else //////tpl_id가 있음
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
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater_parking_bus())
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
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_list_1())
                    .addToBackStack(null).commit()
            }

            "answer_3", "tpl-com-03" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_3())
                    .addToBackStack(null).commit()
            }

            "answer-list_2", "tpl-com-04" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_list_2())
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
        var start: Intent = Intent(this, RobotEventService::class.java)
        this.startService(start)
//            .runCatching {
//                mNavigationManager = NavigationManagerInstance.instance.getNavigationManager()
//                var power: PowerManager = PowerManagerInstance.instance.getPowerManager()
//                power.robotActivation()
//            }
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
        }
	
	else if(id==R.id.enginBtn) {
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