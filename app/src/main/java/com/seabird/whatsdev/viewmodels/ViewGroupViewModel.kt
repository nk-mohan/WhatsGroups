package com.seabird.whatsdev.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.db.GroupRepository
import com.seabird.whatsdev.network.model.GroupModel
import com.seabird.whatsdev.network.model.UpdateViewedGroupResponse
import com.seabird.whatsdev.network.other.Resource
import com.seabird.whatsdev.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewGroupViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val groupRepository: GroupRepository
): ViewModel() {

    private val _reportRes = MutableLiveData<Resource<UpdateViewedGroupResponse>?>()

    val reportRes : LiveData<Resource<UpdateViewedGroupResponse>?>
        get() = _reportRes

    fun updateViewedStatus(groupId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = appRepository.updateViewedGroupStatus(groupId.toString())
            if (response.isSuccessful)
                Log.d(TAG, "updateViewedStatus isSuccessful")
            else
                Log.d(TAG, "updateViewedStatus failed")
        }
    }

    fun reportGroup(groupId: Int) {
        _reportRes.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = appRepository.reportGroup(groupId.toString())
            if (response.isSuccessful)
                _reportRes.postValue(Resource.success(null))
            else
                _reportRes.postValue(Resource.error(response.errorBody().toString(), response.code(), null))
        }
    }

    fun resetReportStatus() {
        _reportRes.postValue(null)
    }

    fun updateFavouriteItem(groupModel: GroupModel) {
        viewModelScope.launch {
            if (isFavouriteItem(groupModel))
                groupRepository.deleteGroup(groupModel.id)
            else
                groupRepository.insertGroup(groupModel)
        }
    }

    fun isFavouriteItem(groupModel: GroupModel): Boolean {
        return groupRepository.isFavoriteGroup(groupModel.id)
    }
}