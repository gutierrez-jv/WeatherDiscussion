package com.example.weatherdiscussion

import android.app.Application
import android.content.Intent
import android.content.ServiceConnection
import com.example.weatherdiscussion.di.AppContainer
import java.util.concurrent.Executor

class WeatherApp : Application() {

    lateinit var container : AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }


}