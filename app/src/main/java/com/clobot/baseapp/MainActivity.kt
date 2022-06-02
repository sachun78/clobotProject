package com.clobot.baseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.clobot.baseapp.databinding.ActivityMainBinding
import com.clobot.baseapp.viewmodel.UserProfileViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private val model: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        model.getAll().observe(this, Observer {
            it -> println(it)
        })
    }
}