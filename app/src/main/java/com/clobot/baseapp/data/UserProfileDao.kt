package com.clobot.baseapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface UserProfileDao {
    @Insert(onConflict = REPLACE)
    fun insert(userProfile: UserProfile)

    @Update
    fun update(userProfile: UserProfile)

    @Delete
    fun delete(userProfile: UserProfile)

    @Query("SELECT * FROM UserProfile")
    fun getAll(): LiveData<List<UserProfile>>
}