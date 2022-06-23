package com.lge.support.second.application.database.pageConfig

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TTSConfigDao {
    @Query("SELECT * FROM tts_config")
    fun getAllTTSConfig(): List<TTSConfig>

    @Query("SELECT * FROM tts_config WHERE page_conf_id = :page_id")
    fun getTTSConfigByPageId(page_id: Int): List<TTSConfig>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTTSConfig(ttsConfs: List<TTSConfig>)

    @Query("DELETE from tts_config")
    fun deleteAll()
}