package com.lge.support.second.application.main.poi

import android.content.Context
import com.lge.robot.platform.util.poi.POIDBManager
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.main.util.FileUtil
import java.io.*
import java.util.ArrayList


class PoiDbManager {
    companion object {
        val TEST_MAP: String = "ROBOMEDIV2_OQC" /*"ROBOMEDIV2_OQC" prebuilt map resource*/
        val TEST_MAP_FLOOR: String = "F3"       /*"F3"*/
        val TEST_MAP_FLOOR_FLOAT: Float = 3.0F  /*"3.0"*/
        val TEST_POI_B1_JSON = "ROBOMEDIV2_OQC_F3.poi"
        val SDCARD_PATH = "/sdcard/porterbot/MAP_LGIDM/"
    }

    private var mPoi: POIDBManager;

    constructor(context: Context) {
        mPoi = POIDBManager(context)
    }

    fun generatePoi() {
        mPoi.generateAllPOI();
    }

    fun getInitPosition(): POI? {
        return mPoi.getInitPosPOI()
    }

    fun registerInitPosForTestMap() {
        mPoi.registerInitFloor(
            0.0f, 0.0f, TEST_MAP_FLOOR_FLOAT,
            TEST_MAP, TEST_MAP_FLOOR, TEST_MAP_FLOOR
        )
    }

    fun copyAssetFile(context: Context) {
        var filename = TEST_POI_B1_JSON
        val assetManager = context.assets
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = assetManager.open(filename)
            out = FileOutputStream(SDCARD_PATH + filename)
            FileUtil.copyFile(`in`, out)
            `in`.close()
            out.flush()
            out.close()
        } catch (e: IOException) {
        } finally {
            `in` = null
            out = null
        }
    }

    fun clearPoiDb() {
        mPoi.dropTable()
        mPoi.dropInitPos()
    }

    fun getAllPoi(): ArrayList<POI>? {
        return mPoi.allPOI
    }
}