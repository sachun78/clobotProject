package com.lge.support.second.application.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.data.pageConfig.PageInfoItem
import com.lge.support.second.application.data.pageConfig.TtsInfoItem
import com.lge.support.second.application.database.PageInfoDao
import kotlinx.coroutines.flow.Flow

class SceneConfigRepo private constructor(
    private val pageInfoDao: PageInfoDao
) {
    fun getPageInfo(pageId: String) = pageInfoDao.getPageInfo(pageId)
    fun getAllPageInfo() = pageInfoDao.getAllPageInfo()
    suspend fun delete() = pageInfoDao.deleteAll()

    companion object {
        private const val TAG = "SceneConfig"
        private var mCurrentPage: String = ""

        @Volatile private var instance: SceneConfigRepo? = null

        @JvmStatic fun getInstance(pageInfoDao: PageInfoDao): SceneConfigRepo =
            instance ?: synchronized(this) {
                instance ?: SceneConfigRepo(pageInfoDao).also {
                    instance = it
                }
            }
    }
}