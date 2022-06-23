package com.lge.support.second.application.repository

import com.lge.support.second.application.database.pageConfig.PageConfig
import com.lge.support.second.application.database.pageConfig.PageConfigDao
import com.lge.support.second.application.database.pageConfig.TTSConfigDao

class PageConfigRepo(
    private val pageConfigDao: PageConfigDao,
    private val ttsConfigDao: TTSConfigDao
) {
    fun getAllPageConfig() = pageConfigDao.getAllPageConfig()
    fun getPageConfigById(id: Int) = pageConfigDao.getPageConfigById(id)
    suspend fun insertAllPageConfig(data: List<PageConfig>) {
        pageConfigDao.insertAllPageConfig(data)
    }
    suspend fun deleteAll() = pageConfigDao.deleteAll()

    companion object {
        private const val TAG: String = "CLOBOT_PageConf"
    }
}