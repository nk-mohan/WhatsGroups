package com.seabird.whatsdev

import android.app.Application
import android.content.Context
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.SharedPreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferenceManager.init(this)
        if (SharedPreferenceManager.getStringValue(AppConstants.DEVICE_ID).isNullOrBlank()) {
            SharedPreferenceManager.setStringValue(AppConstants.DEVICE_ID, System.currentTimeMillis().toString())
        }
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun getContext(): Context {
            return instance!!.applicationContext
        }
    }
}