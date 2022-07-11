package com.lge.support.second.application.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.lge.support.second.application.data.pageConfig.PageInfoItem
import com.lge.support.second.application.util.DATABASE_NAME
import com.lge.support.second.application.util.PAGE_INFO_FILENAME
import com.lge.support.second.application.util.TTS_INFO_FILENAME
import com.lge.support.second.application.worker.SceneConfigWorker
import com.lge.support.second.application.worker.SceneConfigWorker.Companion.KEY_PAGE_FILE
import com.lge.support.second.application.worker.SceneConfigWorker.Companion.KEY_TTS_FILE

@Database(
    entities = arrayOf(
        BatteryEntity::class,
        PageInfoItem::class
    ),
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CommonDatabase : RoomDatabase() {
    abstract fun batteryDao(): BatteryDao
    abstract fun pageInfoDao(): PageInfoDao

    companion object {
        private var INSTANCE: CommonDatabase? = null

        @Volatile private var instance: CommonDatabase? = null

        fun getInstance(context: Context): CommonDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it}
            }
        }

        private fun buildDatabase(context: Context): CommonDatabase {
            return Room.databaseBuilder(context, CommonDatabase::class.java, DATABASE_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("hjbae", "db onCreate !!!!")
                            val request = OneTimeWorkRequestBuilder<SceneConfigWorker>()
                                .setInputData(
                                    workDataOf(
                                        KEY_PAGE_FILE to PAGE_INFO_FILENAME,
                                        KEY_TTS_FILE to TTS_INFO_FILENAME
                                    )
                                ).build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .build()
        }
//        fun getInstance(context: Context): CommonDatabase? {
//            if (INSTANCE == null) {
//                synchronized(CommonDatabase::class) {
//                    INSTANCE = Room.databaseBuilder(
//                        context.applicationContext,
//                        CommonDatabase::class.java,
//                        DATABASE_NAME
//                    ).build()
//                }
//            }
//            return INSTANCE
//        }
    }
}