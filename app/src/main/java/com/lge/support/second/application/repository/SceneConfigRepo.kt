package com.lge.support.second.application.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.data.pageConfig.PageInfoItem
import com.lge.support.second.application.data.pageConfig.TtsInfoItem

class SceneConfigRepo {
    private var pageInfo: ArrayList<PageInfoItem>
    private var ttsInfo : ArrayList<TtsInfoItem>

    init {
        val pageInfoJson = MainActivity.mainContext().assets.open("page_info.json").reader().readText()
        val pageInfoType = object : TypeToken<ArrayList<PageInfoItem>>() {}.type
        pageInfo = Gson().fromJson(pageInfoJson, pageInfoType)

        val ttsInfoJson = MainActivity.mainContext().assets.open("tts_info.json").reader().readText()
        val ttsInfoType = object : TypeToken<ArrayList<TtsInfoItem>>() {}.type
        ttsInfo = Gson().fromJson(ttsInfoJson, ttsInfoType)

        for (pageInfoItem in pageInfo) {
            if (pageInfoItem.is_tts) {
                pageInfoItem.tts_info = ttsInfo.filter {
                    (it.tts_id).contains(pageInfoItem.page_id)
                } as ArrayList<TtsInfoItem>
            }
        }
    }

    fun getCurrPageInfo(pageId: String): PageInfoItem {
        var findData = pageInfo.find {
            it.page_id == pageId
        }
        return findData ?: PageInfoItem()
    }
}