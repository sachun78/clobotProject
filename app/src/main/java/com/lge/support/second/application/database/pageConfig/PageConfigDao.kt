package com.lge.support.second.application.database.pageConfig

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PageConfigDao {
    @Query("SELECT * FROM page_config")
    fun getAllPageConfig(): List<PageConfigEntity>

    @Query("SELECT * FROM page_config WHERE id = :page_id")
    fun getPageConfigById(page_id: Int): PageConfigEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPageConfig(pageConf: PageConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPageConfig(pageConfs: List<PageConfigEntity>)

    @Query("DELETE from page_config")
    fun deleteAll()
}