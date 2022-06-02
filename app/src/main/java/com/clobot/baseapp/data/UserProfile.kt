package com.clobot.baseapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProfile(
    var name: String,
    var phone: String,
    var addr: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}