package com.lge.support.second.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.lge.robot.platform.PlatformUtil.context
import com.lge.robot.platform.navigation.navigation.NavigationManager
import com.lge.robot.platform.power.PowerManager
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.main.managers.robot.NavigationManagerInstance
import com.lge.support.second.application.main.managers.robot.PowerManagerInstance
import com.lge.support.second.application.main.model.TkTestViewModel
import com.lge.support.second.application.main.poi.PoiDbManager

class TestActivity : AppCompatActivity() {
    lateinit var tkTestViewModel2: TkTestViewModel

    private lateinit var mNavigationManager: NavigationManager
    private lateinit var listView: ListView
    private var mInitPos: POI? = null
    private var mPOIs: ArrayList<POI>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        mNavigationManager = NavigationManagerInstance.instance.getNavigationManager()

        mInitPos = context?.let { PoiDbManager(it).getInitPosition() }
        mPOIs  = context?.let { PoiDbManager(it).getAllPoi() }

        listView = findViewById<ListView>(R.id.listview)
        var listItems = arrayListOf<POI>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        findViewById<Button>(R.id.btn_activation).setOnClickListener {
            var power: PowerManager = PowerManagerInstance.instance.getPowerManager()
            power.robotActivation()
            Toast.makeText(this, "Activation !!!", Toast.LENGTH_SHORT).show()
            Log.i("HJBAE", "powerMode: ${power.getPowerMode()}")
        }

        findViewById<Button>(R.id.btn_pois).setOnClickListener {
            listItems = mPOIs ?: arrayListOf<POI>()
            adapter.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { parent, view, pos, id ->
            Toast.makeText(this, "pos:${pos}, id:${id} !!!", Toast.LENGTH_SHORT).show()
        }

//        tkTestViewModel = MainActivity.tkTestViewModel
////        참고 private val viewModel: NavigationModel by viewModels()
//
//        tkTestViewModel.currentValue.observe(this, Observer{
//            Log.i(MainActivity.tk_TAG, "MainActivity - tkTestViewModel - currentValue live Data 변경 : $it")
//        })
//
        var finishButton : Button = findViewById<Button>(R.id.testActivityBtn)
//        var testButton : Button = findViewById<Button>(R.id.testVMBtn)
//
//        testButton.setOnClickListener {
//            tkTestViewModel.updateValue(actionType = ActionType.Test, 1)
//        }
//
        finishButton.setOnClickListener {
            this.finish()
        }
//
//        Log.i(MainActivity.tk_TAG, "testActivity onCreate");
//    }
//
//    override fun onDestroy() {
//        Log.i(MainActivity.tk_TAG, "testActivity onDestroy");
//        super.onDestroy()
//    }
    }
}