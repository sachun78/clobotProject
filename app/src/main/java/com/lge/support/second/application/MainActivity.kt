package com.lge.support.second.application

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import com.example.googlecloudmanager.GoogleSTT
import com.example.googlecloudmanager.GoogleTTS
import com.example.googlecloudmanager.Language
import com.lge.support.second.application.main.data.chatbot.ChatbotApi
import com.lge.support.second.application.databinding.ActivityMainBinding
import com.lge.support.second.application.main.model.TkTestViewModel
import com.lge.support.second.application.main.view.*
import com.lge.support.second.application.main.view.subView.SubScreen
import com.lge.support.second.application.main.model.ChatbotViewModel
import com.lge.support.second.application.main.repository.ChatbotRepository
import java.io.InputStream
import java.util.*

class MainActivity : RobotActivity() {
    private val TAG = MainActivity::class.java.simpleName

    //for viewmodel test log_Tag
    companion object {
        const val tk_TAG: String = "logForTest"
        lateinit var tkTestViewModel: TkTestViewModel

        lateinit var viewModel: ChatbotViewModel

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

    private val chatbotService = ChatbotApi.getInstance()

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

//        var qiBtn : ImageView = findViewById(R.id.qiMessage)
//        qiBtn.visibility = View.INVISIBLE
//        var qiView : View = findViewById(R.id.test_toolbar)
//        var qiVisibility : ImageView = qiView.findViewById(R.id.qiMessage)
        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_main)

        val googleSttCredential: InputStream? = this.resources?.openRawResource(R.raw.credential)
        if (googleSttCredential != null) {
            GoogleSTT.initialize(
                googleSttCredential,
                UUID.randomUUID().toString(),
                Language.Korean
            )

        }
        val googleTtsCredential: InputStream? = this.resources?.openRawResource(R.raw.credential)
        if (googleTtsCredential != null) {
            GoogleTTS.initialize(
                googleTtsCredential,
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
            ChatbotViewModel.Factory(ChatbotRepository(chatbotService))
        ).get(ChatbotViewModel::class.java)


        displays = displayManager.displays

        if (displays.size > 0) {
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
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_main, chat())
                .addToBackStack(null).commit()
            
            viewModel.getResponse("하이 큐아이")
        }
        backBtn.setOnClickListener {
            supportFragmentManager.popBackStack()
        }
        homeBtn.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        korBtn.setOnClickListener {
            setLocate("ko")
            recreate()
            //supportFragmentManager.beginTransaction().detach().attach().commit()
        }
        enBtn.setOnClickListener {
            setLocate("en")
            recreate()
            //refreshFragment(this, supportFragmentManager)
        }

        findViewById<ImageView>(R.id.qiMessage).setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_main, chat())
                .addToBackStack(null).commit()
        }

        ////////////main-Button Listener end////////////

        // ViewModel Overserve TODO(Chatbot Request Base Action)
        viewModel.queryResult.observe(this) {
            Log.d("ViewModel", "chatbot data change, $it")
            if (it == null) {
                return@observe
            }
            GoogleTTS.speak(this, it.data.result.fulfillment.speech[0])
            head.changeText(it.data.result.fulfillment.speech[0] + " (" + it.data.result.fulfillment.custom_code.head + ")")
            it.data.result.fulfillment.messages.forEach { message ->
                Log.d("ViewModel Observe", message.image.toString())
            }
        }
        //viewModel.getResponse("하이 큐아이")
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

    //fragment
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
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
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_main,
                    theater_map()
                ).addToBackStack(null).commit()
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater_map()).commit()
            }

            "theater-parking" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, theater_parking_bus())
                    .addToBackStack(null).commit()
            }

            "answer-1" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, answer_1())
                    .addToBackStack(null).commit()
            }

            "play-clip" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, play_clip())
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
        return super.onOptionsItemSelected(item)
    }
}