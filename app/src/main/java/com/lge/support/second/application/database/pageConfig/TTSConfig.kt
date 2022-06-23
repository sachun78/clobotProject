package com.lge.support.second.application.database.pageConfig

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tts_config")
data class TTSConfig(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "tts_id") val tts_id: String,
    @ColumnInfo(name = "page_conf_id") val page_conf_id: Int,
    @ColumnInfo(name = "item") val item: Int,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "seq") val seq: Int,
    @ColumnInfo(name = "description") val description: String,
)