package com.seabird.whatsdev.ui.groups

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.db.GroupRepository
import com.seabird.whatsdev.network.model.*
import com.seabird.whatsdev.network.other.Resource
import com.seabird.whatsdev.network.repository.AppRepository
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val groupRepository: GroupRepository
): ViewModel() {

    private val _registerRes = MutableLiveData<Resource<RegisterResponse>>()

    val registerRes : LiveData<Resource<RegisterResponse>>
        get() = _registerRes

    var groups = mutableListOf<GroupModel>()
    val addLoader = MutableLiveData<Boolean>()
    val removeLoader = MutableLiveData<Boolean>()
    val fetchingError = MutableLiveData<Boolean>()
    var notifyNewGroupsInsertedLiveData = MutableLiveData<Pair<Int, Int>>()

    private var isFetching = false
    private var currentPage = 0
    private var resultPerPage = 10
    private var totalPage = 1

    init {
        groups = mutableListOf()
        notifyNewGroupsInsertedLiveData = MutableLiveData<Pair<Int, Int>>()
        checkAndRegister()
    }

    fun checkAndRegister() {
        viewModelScope.launch {
            if (SharedPreferenceManager.getStringValue(AppConstants.REFRESH_TOKEN).isNullOrBlank()) {
                if (!SharedPreferenceManager.getBooleanValue(AppConstants.IS_REGISTERED)) {
                    _registerRes.postValue(Resource.loading(null))
                    appRepository.registerUser(
                        RegisterRequest(
                            device_id = SharedPreferenceManager.getStringValue(AppConstants.DEVICE_ID),
                            device_model = Build.MODEL,
                            device_os_version = Build.VERSION.RELEASE
                        )
                    ).let {
                        if (it.isSuccessful) {
                            SharedPreferenceManager.setBooleanValue(AppConstants.IS_REGISTERED, true)
                            it.body()?.user?.let { user ->
                                SharedPreferenceManager.setStringValue(AppConstants.ACCESS_TOKEN, user.access_token)
                                SharedPreferenceManager.setStringValue(AppConstants.REFRESH_TOKEN, user.refresh_token)
                            }
                            _registerRes.postValue(Resource.success(null))
                        } else {
                            _registerRes.postValue(Resource.error(it.errorBody().toString(), it.code(), null))
                        }
                    }
                } else {
                    _registerRes.postValue(Resource.loading(null))
                    appRepository.loginUser(
                        LoginRequest(device_id = SharedPreferenceManager.getStringValue(AppConstants.DEVICE_ID))
                    ).let {
                        if (it.isSuccessful) {
                            SharedPreferenceManager.setBooleanValue(AppConstants.IS_REGISTERED, true)
                            it.body()?.user?.let { user ->
                                SharedPreferenceManager.setStringValue(AppConstants.ACCESS_TOKEN, user.access_token)
                                SharedPreferenceManager.setStringValue(AppConstants.REFRESH_TOKEN, user.refresh_token)
                            }
                            _registerRes.postValue(Resource.success(null))
                        } else {
                            _registerRes.postValue(Resource.error(it.errorBody().toString(), it.code(), null))
                        }
                    }
                }
            } else {
                _registerRes.postValue(Resource.success(null))
            }
        }
    }

    private fun setUserListFetching(isFetching: Boolean) {
        this.isFetching = isFetching
    }

    fun getUserListFetching(): Boolean {
        return isFetching
    }

    fun getGroupList() {
        if (lastPageFetched())
            return
        updateLoaderStatus()
        fetchingError.value = false
        viewModelScope.launch(Dispatchers.IO) {
            currentPage += 1
            setUserListFetching(true)
            val response = appRepository.getRecentGroups(currentPage, resultPerPage)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data
                val meta = response.body()!!.meta
                totalPage = meta.total_pages
                val startPosition = groups.size
                groups.addAll(data)
                viewModelScope.launch(Dispatchers.Main) {
                    removeLoader.postValue(true)
                    notifyNewGroupsInsertedLiveData.postValue(Pair(startPosition, data.size))
                    updateLoaderStatus()
                }
            } else {
                currentPage -= 1
                viewModelScope.launch(Dispatchers.Main) {
                    removeLoader.postValue(true)
                    fetchingError.value = true
                }
            }
            setUserListFetching(false)
        }
    }

    private fun updateLoaderStatus() {
        if (lastPageFetched())
            removeLoader.postValue(true)
        else
            addLoader.postValue(true)
    }

    fun addLoaderToTheList() {
        addLoader.postValue(true)
    }

    fun lastPageFetched() = currentPage >= totalPage


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

    fun resetResult() {
        isFetching = false
        currentPage = 0
        resultPerPage = 10
        totalPage = 1
        groups.clear()
    }
}