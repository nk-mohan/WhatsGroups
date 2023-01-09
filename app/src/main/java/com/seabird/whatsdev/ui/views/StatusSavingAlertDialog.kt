package com.seabird.whatsdev.ui.views

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.seabird.whatsdev.R
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.StatusSaveDialogBinding
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.utils.AppUtils
import com.seabird.whatsdev.utils.DownloadUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class StatusSavingAlertDialog @Inject constructor(private var activity: Activity) {

    private var alertDialog: AlertDialog? = null

    private val saveScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var saveJob: Job? = null

    fun saveStatuses(uris: ArrayList<Uri>) {
        val localData = uris.toMutableList()
        val dialogBuilder = AlertDialog.Builder(activity, R.style.CustomAlertDialog)
        val inflater: LayoutInflater = activity.layoutInflater
        val dialogBinding = StatusSaveDialogBinding.inflate(inflater)

        dialogBuilder.apply {
            setCancelable(false)
            setView(dialogBinding.root)
        }

        alertDialog = dialogBuilder.create()
        alertDialog?.show()

        dialogBinding.cancelButton.setSafeOnClickListener {
            saveJob?.cancel()
            alertDialog?.dismiss()
        }

        adjustAlertDialogWidth(activity, alertDialog)

        saveJob = saveScope.launch {
            localData.forEachIndexed { index, uri ->
                launch(Dispatchers.Main) {
                    dialogBinding.downloadCount.text = String.format(activity.getString(R.string.downloading_count), index, localData.size)
                }
                downloadStatus(uri)?.let {
                    launch(Dispatchers.Main) {
                        dialogBinding.downloadCount.text = String.format(activity.getString(R.string.downloading_count), index + 1, localData.size)
                        if (index + 1 == localData.size) {
                            Toast.makeText(activity, activity.getString(R.string.status_saved), Toast.LENGTH_SHORT).show()
                            alertDialog?.dismiss()
                        }
                    }
                }
            }
        }

        saveJob?.start()

    }

    private fun adjustAlertDialogWidth(activity: Activity, alertDialog: AlertDialog?) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog?.window!!.attributes)
        layoutParams.width = (AppUtils.getScreenWidth(activity) * 0.90).toInt()
        alertDialog.window!!.attributes = layoutParams
    }

    private fun downloadStatus(uri: Uri): File? {
        try {
            val filename = File(uri.path).name
            val inputStream = activity.contentResolver.openInputStream(uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                inputStream?.let {
                    val environment = if (filename.contains(".jpg")) Environment.DIRECTORY_PICTURES else Environment.DIRECTORY_MOVIES
                    val mimeType = if (filename.contains(".jpg")) "image/jpg" else "video/mp4"
                    val mediaStoreUri = if (filename.contains(".jpg")) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    val relativeLocation = environment + File.separator + "WhatsApp Status"
                    val file = File(Environment.getExternalStoragePublicDirectory(relativeLocation), filename)
                    return if (!file.exists()) {
                        val path = copyStream(activity, inputStream, filename, relativeLocation, mimeType, mediaStoreUri).path
                         if (path != null)
                            File(path)
                        else
                            null
                    } else
                        file
                }
            } else {
                val downloadFile = AppUtils.getDownloadedFile(activity, filename)
                val fileOutputStream = FileOutputStream(downloadFile)
                inputStream?.let {
                    DownloadUtils.copyStream(inputStream, fileOutputStream)
                    fileOutputStream.close()
                    inputStream.close()
                    val mimeType = MimeTypeMap.getFileExtensionFromUrl(downloadFile.absolutePath)
                    MediaScannerConnection.scanFile(activity, arrayOf(downloadFile.absolutePath), arrayOf(mimeType), null)
                    return downloadFile
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage)
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Throws(IOException::class)
    fun copyStream(context: Context, inputStream: InputStream, displayName: String, relativeLocation: String, mimeType: String, mediaStoreUri: Uri): Uri {

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        }

        var uri: Uri? = null

        return runCatching {
            with(context.contentResolver) {
                insert(mediaStoreUri, values)?.also {
                    uri = it // Keep uri reference so it can be removed on failure

                    openOutputStream(it)?.use { outputStream ->
                        inputStream.use { inputStreamUse ->
                            val buffer = ByteArray(1024)
                            var bytesRead: Int
                            while (inputStreamUse.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                        }
                    } ?: throw IOException("Failed to open output stream.")

                } ?: throw IOException("Failed to create new MediaStore record.")
            }
        }.getOrElse {
            uri?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                context.contentResolver.delete(orphanUri, null, null)
            }

            throw it
        }
    }

}