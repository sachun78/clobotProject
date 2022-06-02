package com.clobot.baseapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.clobot.baseapp.data.UserProfile
import com.clobot.baseapp.data.UserProfileDao

@Database(entities = [UserProfile::class], version = 1, exportSchema = false)
abstract  class AppDatabase: RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "clobot"
                    ).build()
                }
            }
            return instance
        }
    }
}