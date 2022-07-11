package com.lge.support.second.application.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lge.support.second.application.data.pageConfig.PageInfoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PageInfoDao {
    @Query("SELECT * FROM page_info WHERE page_id = :page_id")
    fun getPageInfo(page_id: String): Flow<PageInfoItem>

    @Query("SELECT * FROM page_info")
    fun getAllPageInfo(): Flow<List<PageInfoItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllPageInfo(pageInfos: List<PageInfoItem>)

    @Query("DELETE from page_info")
    suspend fun deleteAll()
}