package com.seabird.whatsdev.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewGroupViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {

    fun updateViewedStatus(groupId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = appRepository.updateViewedGroupStatus(groupId.toString())
            if (response.isSuccessful)
                Log.d(TAG, "updateViewedStatus isSuccessful")
            else
                Log.d(TAG, "updateViewedStatus failed")
        }
    }
}