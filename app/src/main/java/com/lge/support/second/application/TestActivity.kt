package com.lge.support.second.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.lge.aip.engine.facedetection.LGFaceDetectionJNI
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.managers.robot.PoiDbManager

class TestActivity : AppCompatActivity() {
    private var mPOIs: ArrayList<POI>? = null
    private var fd: LGFaceDetectionJNI = LGFaceDetectionJNI()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//        mInitPos = PoiDbManager(this).getInitPosition()
        mPOIs = PoiDbManager(this).getAllPoi()

        var listView = findViewById<ListView>(R.id.listview)
        var listItems = arrayListOf<POI>()
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        fd.create()
        findViewById<Button>(R.id.btn_undocking).setOnClickListener {
            MainActivity.viewModel.undockingRequest()
        }

        findViewById<Button>(R.id.btn_docking).setOnClickListener {
            MainActivity.viewModel.dockingRequest()
        }

        findViewById<Button>(R.id.btn_pois).setOnClickListener {
            listItems.addAll(mPOIs ?: arrayListOf<POI>())
            Log.i("HJBAE", "data: $listItems")
            adapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.btn_cruise).setOnClickListener {
            MainActivity.viewModel.cruiseRequest()
        }

        listView.setOnItemClickListener { parent, view, pos, id ->
            mPOIs?.let { MainActivity.viewModel.move(it.get(id.toInt())) }
            Toast.makeText(this, "pos:${pos}, id:${id}", Toast.LENGTH_SHORT).show()
        }

        MainActivity.viewModel.isMoving.observe(this) {
            if (it == true) {
                MainActivity.subTest.findViewById<TextView>(R.id.sub_textView)
                    .setText("MOVING PAGE")
            } else {
                MainActivity.subTest.findViewById<TextView>(R.id.sub_textView)
                    .setText("WAITING PAGE")
            }
        }

        MainActivity.viewModel.isDocking.observe(this) {
            if (it == true) {
                MainActivity.subTest.findViewById<TextView>(R.id.sub_textView)
                    .setText("Docking...")
            } else {
                MainActivity.subTest.findViewById<TextView>(R.id.sub_textView)
                    .setText("WAITING PAGE")
            }
        }

        MainActivity.viewModel.queryResult.observe(this) {
            if (it != null) {
                MainActivity.viewModel.ttsSpeak(this, it.data.result.fulfillment.speech[0])
            }
        }

        var finishButton: Button = findViewById<Button>(R.id.testActivityBtn)

        finishButton.setOnClickListener {
            this.finish()
        }
    }
}