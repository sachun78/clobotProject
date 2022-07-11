package com.lge.support.second.application.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lge.support.second.application.data.pageConfig.TtsInfoItem

class Converters {
    @TypeConverter
    fun listToJson(value: List<TtsInfoItem>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String): List<TtsInfoItem> {
        val type = object: TypeToken<List<TtsInfoItem>>() {}.type
        return Gson().fromJson(value, type)
    }
}