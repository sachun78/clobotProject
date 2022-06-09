package com.lge.support.second.application.main.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(BatteryEntity::class), version = 1, exportSchema = false)
abstract class BatteryDatabase : RoomDatabase() {
    abstract fun batteryDao(): BatteryDao

    companion object {
        private var INSTANCE: BatteryDatabase? = null

        fun getInstance(context: Context): BatteryDatabase? {
            if (INSTANCE == null) {
                synchronized(BatteryDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        BatteryDatabase::class.java,
                        "battery.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}