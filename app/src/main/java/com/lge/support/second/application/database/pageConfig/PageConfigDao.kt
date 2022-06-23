package com.lge.support.second.application.database.pageConfig

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PageConfigDao {
    @Query("SELECT * FROM page_config")
    fun getAllPageConfig(): Flow<List<PageConfig>>

    @Query("SELECT * FROM page_config WHERE id = :page_id")
    fun getPageConfigById(page_id: Int): Flow<PageConfig>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageConfig(pageConf: PageConfig)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPageConfig(pageConfs: List<PageConfig>)

    @Query("DELETE from page_config")
    suspend fun deleteAll()
}