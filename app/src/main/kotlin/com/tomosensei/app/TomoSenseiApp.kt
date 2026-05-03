package com.tomosensei.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TomoSenseiApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
