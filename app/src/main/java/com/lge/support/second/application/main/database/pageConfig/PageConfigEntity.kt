package com.lge.support.second.application.main.database.pageConfig

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "page_config")
data class PageConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "page_id") val type: String,
    @ColumnInfo(name = "language") val soc: Int,
    @ColumnInfo(name = "is_srt") val soh: Boolean,
    @ColumnInfo(name = "is_chatbot_entry") val is_chatbot_entry: Boolean,
    @ColumnInfo(name = "is_chatbot_req") val is_chatbot_req: Boolean,
    @ColumnInfo(name = "is_croms") val is_croms: Boolean,
    @ColumnInfo(name = "is_back_src") val is_back_src: Boolean,
    @ColumnInfo(name = "is_tts") val is_tts: Boolean,
    @ColumnInfo(name = "back_src_id") val back_src_id: Int,
)