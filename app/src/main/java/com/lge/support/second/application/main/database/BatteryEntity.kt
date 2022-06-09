package com.lge.support.second.application.main.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_history")

data class BatteryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "soc") val soc: Int,
    @ColumnInfo(name = "soh") val soh: Int,
    @ColumnInfo(name = "temperature") val temperature: Int,
    @ColumnInfo(name = "plugged") val plugged: Int,
    @ColumnInfo(name = "time") val contents: Long
)
