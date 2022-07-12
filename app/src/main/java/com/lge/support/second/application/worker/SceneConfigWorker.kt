package com.lge.support.second.application.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lge.support.second.application.MainApplication
import com.lge.support.second.application.data.pageConfig.PageInfoItem
import com.lge.support.second.application.data.pageConfig.TtsInfoItem
import com.lge.support.second.application.database.CommonDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SceneConfigWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        try {
            val pageFileName = inputData.getString(KEY_PAGE_FILE)
            val ttsFileName = inputData.getString(KEY_TTS_FILE)

            Log.d(TAG, "SceneConfigWorker Start")
            if (pageFileName != null && ttsFileName != null) {
                var pageData: ArrayList<PageInfoItem> = arrayListOf()
                var ttsData: ArrayList<TtsInfoItem> = arrayListOf()

                applicationContext.assets.open(pageFileName).use {
                    val pageInfoJson = it.reader().readText()
                    val pageInfoType = object : TypeToken<ArrayList<PageInfoItem>>() {}.type
                    pageData = Gson().fromJson<ArrayList<PageInfoItem>>(pageInfoJson, pageInfoType)
                }

                applicationContext.assets.open(ttsFileName).use {
                    val ttsInfoJson = it.reader().readText()
                    val ttsInfoType = object : TypeToken<ArrayList<TtsInfoItem>>() {}.type
                    ttsData = Gson().fromJson<ArrayList<TtsInfoItem>>(ttsInfoJson, ttsInfoType)
                }

                for (pageInfoItem in pageData) {
                    if (pageInfoItem.is_tts) {
                        pageInfoItem.tts_info = ttsData.filter {
                            (it.tts_id).contains(pageInfoItem.page_id)
                        }
                    }
                }
                val database = CommonDatabase.getInstance(applicationContext)
                database.pageInfoDao().insertAllPageInfo(pageData)

		Log.d(TAG, "SceneConfigWorker success !!!")
                Result.success()
            } else {
                Log.e(TAG, "Error SceneConfigWorker - no valid filename")
                Result.failure()
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error SceneConfigWorker: ", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SceneConfigWorker"
        const val KEY_PAGE_FILE = "PAGE_INFO_FILENAME"
        const val KEY_TTS_FILE = "TTS_INFO_FILENAME"
    }
}