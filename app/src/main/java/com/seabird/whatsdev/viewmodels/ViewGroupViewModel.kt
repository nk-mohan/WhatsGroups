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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewGroupViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val groupRepository: GroupRepository
): ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d(TAG, "exceptionHandler: $exception")
    }

    private val _reportRes = MutableLiveData<Resource<UpdateViewedGroupResponse>?>()

    val reportRes : LiveData<Resource<UpdateViewedGroupResponse>?>
        get() = _reportRes

    var favouriteStatusUpdated = MutableLiveData<Boolean?>()

    fun updateViewedStatus(groupId: Int) {
        viewModelScope.launch(exceptionHandler) {
            val response = appRepository.updateViewedGroupStatus(groupId.toString())
            if (response.isSuccessful)
                Log.d(TAG, "updateViewedStatus isSuccessful")
            else
                Log.d(TAG, "updateViewedStatus failed")
        }
    }

    fun reportGroup(groupId: Int) {
        _reportRes.postValue(Resource.loading(null))
        viewModelScope.launch(exceptionHandler) {
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

    fun resetFavouriteStatus() {
        favouriteStatusUpdated.postValue(null)
    }

    fun updateFavouriteItem(groupModel: GroupModel) {
        viewModelScope.launch {
            if (isFavouriteItem(groupModel)) {
                groupRepository.deleteGroup(groupModel.id)
                favouriteStatusUpdated.postValue(false)
            } else {
                groupRepository.insertGroup(groupModel)
                favouriteStatusUpdated.postValue(true)
            }
        }
    }

    fun isFavouriteItem(groupModel: GroupModel): Boolean {
        return groupRepository.isFavoriteGroup(groupModel.id)
    }
}