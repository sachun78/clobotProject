package com.lge.support.second.application

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.lge.robot.platform.data.SLAM3DPos
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.managers.robot.PoiDbManager

class TestActivity : AppCompatActivity() {
    private var mPOIs: ArrayList<POI>? = null
    lateinit var image1: Bitmap
    lateinit var canvas: Canvas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mPOIs = PoiDbManager(this).getAllPoi()

        var listView = findViewById<ListView>(R.id.listview)
        var listItems = arrayListOf<POI>()
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        //fd.create()
        findViewById<Button>(R.id.btn_undocking).setOnClickListener {
            MainActivity.robotViewModel.undockingRequest()
        }

        findViewById<Button>(R.id.btn_docking).setOnClickListener {
            MainActivity.robotViewModel.dockingRequest()
        }

        findViewById<Button>(R.id.btn_pois).setOnClickListener {
            listItems.addAll(mPOIs ?: arrayListOf<POI>())
            Log.i("HJBAE", "data: $listItems")
            adapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.btn_cruise).setOnClickListener {
            MainActivity.robotViewModel.cruiseRequest()
        }

        listView.setOnItemClickListener { parent, view, pos, id ->
            mPOIs?.let { MainActivity.robotViewModel.move(it.get(id.toInt())) }
            Toast.makeText(this, "pos:${pos}, id:${id}", Toast.LENGTH_SHORT).show()
        }

        MainActivity.robotViewModel.isMoving.observe(this) {
            if (it == true) {
                MainActivity.subTest.findViewById<TextView>(R.id.sub_textView)
                    .setText("MOVING PAGE")
            } else {
                MainActivity.subTest.findViewById<TextView>(R.id.sub_textView)
                    .setText("WAITING PAGE")
            }
        }

        MainActivity.robotViewModel.isDocking.observe(this) {
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
                MainActivity.viewModel.ttsSpeak(this, it.speech[0])
            }
        }

        var finishButton: Button = findViewById<Button>(R.id.testActivityBtn)

        finishButton.setOnClickListener {
            this.finish()
        }

        image1 = BitmapFactory.decodeResource(resources, R.drawable.clobot_ipark_f7);
        val temp = Bitmap.createBitmap(image1, 450, 480, 140, 40)
        canvas = Canvas(temp)
        canvas.drawBitmap(temp, 0f, 0f, null)

        findViewById<ImageView>(R.id.imageView123).setImageBitmap(temp)

        MainActivity.robotViewModel.mSLAM3DPos.observe(this) {
            draw(it, 450, 480, 140, 40)
        }
    }

    private fun draw(pos: SLAM3DPos, x: Int, y: Int, width: Int, height: Int) {
        // map에서 bitmap Crop
        val temp = Bitmap.createBitmap(image1, x, y, width, height)
        canvas.drawBitmap(temp, 0f, 0f, null)

        val drawable = getDrawable(R.drawable.ic_robot_body1) as VectorDrawable

        drawable.setBounds(
            0, 0, 12, 12,
        )


        canvas.save();
        // 510 -> y좌표 시작점 480 + height 40
        canvas.translate(
            (pos.robotPos.x * 10 - x).toFloat(),
            ((y + height) - pos.robotPos.y * 10).toFloat()
        )
        // vector img size 12/2 = 6f
        canvas.rotate(
            (pos.robotPos.deg * -1).toFloat(),
            6f,
            6f
        )
//        drawable.draw(canvas)

        canvas.restore()

        canvas.drawBitmap(
            drawable.toBitmap(12, 12), (pos.robotPos.x * 10 - x).toFloat(),
            ((y + height) - pos.robotPos.y * 10).toFloat(), null
        )

        findViewById<ImageView>(R.id.imageView123).invalidate()
    }
}