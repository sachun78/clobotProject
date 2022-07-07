package com.lge.support.second.application.data.pageConfig

data class PageInfoItem(
    val chat_required: Boolean = false,
    val entry_by_chat: Boolean = false,
    val id: Int = -1,
    val is_tts: Boolean = false,
    val language: Int = 0,
    val page_id: String = "",
    val page_name: String = "",
    val page_type: String = "",
    val page_wait: Int = 0,
    val rank: Boolean = false,
    val sub_yn: Boolean = false,
    var tts_info: ArrayList<TtsInfoItem> = arrayListOf()
)