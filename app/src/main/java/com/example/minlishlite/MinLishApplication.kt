package com.example.minlishlite

import android.app.Application
import com.example.minlishlite.di.AppContainer
import com.example.minlishlite.di.AppDataContainer

class MinLishApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
