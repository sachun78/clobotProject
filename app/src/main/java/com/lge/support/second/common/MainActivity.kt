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
import androidx.lifecycle.Observer
import com.lge.support.second.application.databinding.ActivityMainBinding
import com.lge.support.second.application.main.model.ActionType
import com.lge.support.second.application.main.model.TkTestViewModel
import com.lge.support.second.application.main.view.*
import com.lge.support.second.application.main.view.subView.SubScreen
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


    private lateinit var language_code : String

    //display
    private lateinit var displayManager: DisplayManager
    private lateinit var displays: Array<Display>
    lateinit var subTest : SubScreen
    //lateinit var subTest2 : SubScreen2


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
        var fragment : Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_main)


        var micBtn : Button = findViewById(R.id.micBtn)
        var homeBtn : Button = findViewById(R.id.homeBtn)
        var backBtn : Button = findViewById(R.id.backBtn)
        var korBtn : Button = findViewById(R.id.korBtn)
        var enBtn : Button = findViewById(R.id.enBtn)
        //var qiInfoImg : ImageView = findViewById(R.id.qiMessage)
        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager


        if(displayManager != null) {

            displays = displayManager.displays

            if (displays.size > 0) {
                subTest = SubScreen(this, displays[1])
                //subTest.show()
            }
        }

        /////////////////////////////////viewmodel test/////////////////////////////////
        //tkTestViewModel = ViewModelProvider(this).get(TkTestViewModel::class.java)
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
    fun changeFragment(index : Int){
        when(index) {
            1 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, play()).addToBackStack(null).commit()
            }

            2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main, theater()).addToBackStack(null).commit()

            }

            3 -> {
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