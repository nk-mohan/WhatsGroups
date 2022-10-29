package com.seabird.whatsdev.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import java.io.File

object AppUtils {

    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = activity.display
            display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics)
        }
        return displayMetrics.widthPixels
    }

    private fun getExternalStorage(applicationContext: Context): File {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) Environment.getExternalStorageDirectory() else applicationContext.filesDir
        } else applicationContext.externalMediaDirs[0]
    }
}