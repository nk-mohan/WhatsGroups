package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatusSaverViewModel : ViewModel() {

    val imageList = MutableLiveData<ArrayList<Uri>>()
    val videoList = MutableLiveData<ArrayList<Uri>>()

    fun getWhatsUpFolder(): String {
        return if (Build.VERSION.SDK_INT >= 29) {
            "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
        } else{
            "WhatsApp%2FMedia%2F.Statuses";
        }
    }

    fun readSDK30(tree: DocumentFile) {
        viewModelScope.launch(Dispatchers.IO) {
            val uriImageList  = arrayListOf<Uri>()
            val uriVideoList  = arrayListOf<Uri>()
            listFiles(tree).forEach { uri ->
                if (uri.path!!.contains(".jpg"))
                    uriImageList.add(uri)
                else
                    uriVideoList.add(uri)
            }
            imageList.postValue(uriImageList)
            videoList.postValue(uriVideoList)
        }
    }

    private fun listFiles(folder: DocumentFile): List<Uri> {
        return if (folder.isDirectory) {
            folder.listFiles().mapNotNull { file ->
                if (file.name != null && !file.name!!.contains("nomedia")) file.uri else null
            }
        } else {
            emptyList()
        }
    }
}