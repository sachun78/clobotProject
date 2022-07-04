package com.lge.support.second.application.data.pageConfig

data class PageInfoItem(
    val back_src_id: Int,
    val id: Int,
    val is_back_src: Boolean,
    val is_chatbot_entry: Boolean,
    val is_chatbot_req: Boolean,
    val is_croms: Boolean,
    val is_srt: Boolean,
    val is_tts: Boolean,
    val language: Int,
    val page_id: String,
    var ttsInfo: ArrayList<TtsInfoItem>
)