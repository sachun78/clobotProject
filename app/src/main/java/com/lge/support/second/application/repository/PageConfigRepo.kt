package com.lge.support.second.application.repository

import android.util.Log
import com.lge.support.second.application.MainActivity.Companion.mainContext
import com.lge.support.second.application.database.CommonDatabase
import com.lge.support.second.application.database.pageConfig.PageConfig
import com.lge.support.second.application.database.pageConfig.PageConfigDao
import com.lge.support.second.application.database.pageConfig.TTSConfig
import com.lge.support.second.application.database.pageConfig.TTSConfigDao
import jxl.Sheet
import jxl.Workbook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

class PageConfigRepo(
    private val pageConfigDao: PageConfigDao,
    private val ttsConfigDao: TTSConfigDao
) {
    private var mPageConfStream: InputStream? = null
    private var mWorkBook: Workbook? = null

    init {
        mPageConfStream = mainContext().resources.assets.open("page_config_info.xls")
        mWorkBook = Workbook.getWorkbook(mPageConfStream)
    }

    suspend fun doUpdata() {
        if (!isNewVersion()) {
            Log.d(TAG, "don't update. continue....")
        } else {
            updatePageConfig()
            updateTTSConfig()
        }
    }

    private suspend fun isNewVersion(): Boolean {
        try {
            // version check
            val version: Int = mWorkBook?.getSheet("version")?.getCell(1, 0)?.contents?.toInt() ?: 1
            Log.d(TAG, "version: $version")

            //retValue = if (version != VERSION) {
            return if (version > 0) {
                withContext(Dispatchers.IO) {
                    Log.i(TAG, "deleteAll !!!!");
                    pageConfigDao.deleteAll()
                    ttsConfigDao.deleteAll()
                }
                true
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return true
        }
    }

    private suspend fun updatePageConfig(): Boolean {
        try {
            var sheet: Sheet? = mWorkBook?.getSheet("page_config")
            var colTotal = sheet?.columns ?: 0
            var rowTotal = sheet?.rows ?: 0
            val rowStartIdx = 1
            var pageConfs: MutableList<PageConfig> = mutableListOf<PageConfig>()

            Log.d(TAG,"coltotal: $colTotal, rowTotal: $rowTotal")

            for (idx in rowStartIdx until rowTotal) {
                var rowContents = sheet?.getRow(idx) ?: arrayOf()
                if (rowContents.size > 0) {
                    var pageConf: PageConfig = PageConfig(
                        rowContents[0].contents.toInt(),
                        rowContents[1].contents.toString(),
                        rowContents[2].contents.toInt(),
                        rowContents[3].contents.toBoolean(),
                        rowContents[4].contents.toBoolean(),
                        rowContents[5].contents.toBoolean(),
                        rowContents[6].contents.toBoolean(),
                        rowContents[7].contents.toBoolean(),
                        rowContents[8].contents.toBoolean(),
                        rowContents[9].contents.toInt()
                    )
                    pageConfs.add(pageConf)
                }
            }
            Log.d(TAG, "data size: ${pageConfs.size}")
            withContext(Dispatchers.IO) {
                pageConfigDao.insertAllPageConfig(pageConfs)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private suspend fun updateTTSConfig(): Boolean
    {
        try {
            var sheet: Sheet? = mWorkBook?.getSheet("tts_config")
            var colTotal = sheet?.columns ?: 0
            var rowTotal = sheet?.rows ?: 0
            val rowStartIdx = 1
            var ttsConfs: MutableList<TTSConfig> = mutableListOf<TTSConfig>()

            for (idx in rowStartIdx until rowTotal) {
                var rowContents = sheet?.getRow(idx) ?: arrayOf()
                if (rowContents.size > 0) {
                    var ttsConf: TTSConfig = TTSConfig(
                        rowContents[0].contents.toInt(),
                        rowContents[1].contents.toString(),
                        rowContents[2].contents.toInt(),
                        rowContents[3].contents.toInt(),
                        rowContents[4].contents.toInt(),
                        rowContents[5].contents.toInt(),
                        rowContents[6].contents.toString(),
                    )
                    ttsConfs.add(ttsConf)
                }
            }
            withContext(Dispatchers.IO) {
                ttsConfigDao.insertAllTTSConfig(ttsConfs)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    companion object {
        private const val TAG: String = "CLOBOT_PageConf"
        private const val VERSION: Int = 1
    }
}