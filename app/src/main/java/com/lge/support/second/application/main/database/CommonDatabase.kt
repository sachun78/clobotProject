package com.lge.support.second.application.main.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(BatteryEntity::class), version = 1, exportSchema = false)
abstract class CommonDatabase : RoomDatabase() {
    abstract fun batteryDao(): BatteryDao

    companion object {
        private var INSTANCE: CommonDatabase? = null

        fun getInstance(context: Context): CommonDatabase? {
            if (INSTANCE == null) {
                synchronized(CommonDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CommonDatabase::class.java,
                        "robot.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}