package com.lge.support.second.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.Observer
import com.lge.support.second.application.MainActivity.Companion.tkTestViewModel
import com.lge.support.second.application.main.model.ActionType
import com.lge.support.second.application.main.model.TkTestViewModel

class TestActivity : AppCompatActivity() {
    lateinit var tkTestViewModel2: TkTestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

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