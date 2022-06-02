package com.clobot.baseapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.clobot.baseapp.data.UserProfile
import com.clobot.baseapp.repository.UserProfileRepo

class UserProfileViewModel(application: Application): AndroidViewModel(application) {
    private val repo = UserProfileRepo(application)
    private val items = repo.getAll()

    fun insert(userProfile: UserProfile) {
        repo.insert(userProfile)
    }

    fun update(userProfile: UserProfile) {
        repo.update(userProfile)
    }

    fun delete(userProfile: UserProfile) {
        repo.delete(userProfile)
    }

    fun getAll(): LiveData<List<UserProfile>> {
        return items
    }
}