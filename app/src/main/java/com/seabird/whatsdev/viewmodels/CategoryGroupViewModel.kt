package com.seabird.whatsdev.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.network.model.GroupModel
import com.seabird.whatsdev.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryGroupViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {
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
    }

    private fun setUserListFetching(isFetching: Boolean) {
        this.isFetching = isFetching
    }

    fun getUserListFetching(): Boolean {
        return isFetching
    }

    fun getGroupList(categoryName: String) {
        if (lastPageFetched())
            return
        updateLoaderStatus()
        fetchingError.value = false
        viewModelScope.launch(Dispatchers.IO) {
            currentPage += 1
            setUserListFetching(true)
            val response = appRepository.getCategoryGroups(currentPage, resultPerPage, categoryName)
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

    fun resetResult() {
        isFetching = false
        currentPage = 0
        resultPerPage = 10
        totalPage = 1
        groups.clear()
    }
}