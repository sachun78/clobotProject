package com.lge.support.second.application.data.pageConfig

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "page_info")
data class PageInfoItem(
    @PrimaryKey val id: Int = -1,
    val chat_required: Boolean = false,
    val entry_by_chat: Boolean = false,
    val is_tts: Boolean = false,
    val language: Int = 0,
    val page_id: String = "",
    val page_name: String = "",
    val page_type: String = "",
    val page_wait: Int = 0,
    val rank: Boolean = false,
    val sub_yn: Boolean = false,
    var tts_info: List<TtsInfoItem> = listOf()
)