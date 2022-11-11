package com.seabird.whatsdev.ui.statussaver

import android.app.Activity
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
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
                    val mimeType = MimeTypeMap.getFileExtensionFromUrl(it.absolutePath)
                    MediaScannerConnection.scanFile(activity, arrayOf(it.absolutePath), arrayOf(mimeType), null)
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
            val downloadFile = AppUtils.getDownloadedFile(activity, filename)
            val inputStream = activity.contentResolver.openInputStream(uri)
            val fileOutputStream = FileOutputStream(downloadFile)
            inputStream?.let {
                DownloadUtils.copyStream(inputStream, fileOutputStream)
                fileOutputStream.close()
                inputStream.close()
                return downloadFile
            }
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage)
        }
        return null
    }
}