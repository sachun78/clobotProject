package com.lge.support.second.application.main.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BatteryDao {
    @Query("SELECT * FROM battery_history")
    fun getAllBatteryHistory(): List<BatteryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBatteryInfo(battery: BatteryEntity)

    @Query("DELETE from battery_history")
    fun deleteAll()
}