package com.clobot.baseapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.clobot.baseapp.data.UserProfile
import com.clobot.baseapp.data.UserProfileDao
import com.clobot.baseapp.database.AppDatabase

class UserProfileRepo(application: Application) {
    private val userProfileDao: UserProfileDao
    private val userProfileList: LiveData<List<UserProfile>>

    init {
        var db = AppDatabase.getInstance((application))
        userProfileDao = db!!.userProfileDao()
        userProfileList = userProfileDao.getAll()
    }

    fun insert(userProfile: UserProfile) {
        userProfileDao.insert(userProfile)
    }

    fun update(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
    }

    fun delete(id: Int) {
        userProfileDao.delete(id)
    }

    fun getAll(): LiveData<List<UserProfile>> {
        return userProfileDao.getAll()
    }
}