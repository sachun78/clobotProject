package com.lge.support.second.application.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lge.support.second.application.database.pageConfig.PageConfigDao
import com.lge.support.second.application.database.pageConfig.PageConfig
import com.lge.support.second.application.database.pageConfig.TTSConfigDao
import com.lge.support.second.application.database.pageConfig.TTSConfig

@Database(
    entities = arrayOf(
        BatteryEntity::class,
        PageConfig::class,
        TTSConfig::class),
    version = 1, exportSchema = false
)
abstract class CommonDatabase : RoomDatabase() {
    abstract fun batteryDao(): BatteryDao
    abstract fun pageConfigDao(): PageConfigDao
    abstract fun ttsConfigDao(): TTSConfigDao

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