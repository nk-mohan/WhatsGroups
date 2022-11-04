package com.seabird.whatsdev.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import com.seabird.whatsdev.R
import java.io.File
import java.io.IOException

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

    fun getDownloadedFile(context: Context, fileName: String): File {
        val parentPath = getPath(context, context.getString(R.string.status_folder_label))
        val parentFolder = createFolderIfNotExist(parentPath)
        val file = File("$parentFolder/$fileName")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }


    /**
     * Returns the directory inside which a new file will be created.
     *
     * @param context    The parent context.
     * @param folderName The name of the folder in which the recorded audio will be written.
     * @return The parent pathname string.
     */
    private fun getPath(context: Context, folderName: String): String {
        return getExternalStorage(context).absolutePath + File.separator + context.getString(R.string.app_name) + File.separator + folderName
    }

    /**
     * Creates folder if not exist
     *
     * @param path Path of the folder
     */
    private fun createFolderIfNotExist(path: String): File {
        val folder = File(path)
        if (!folder.exists())
            folder.mkdirs()
        return folder
    }
}