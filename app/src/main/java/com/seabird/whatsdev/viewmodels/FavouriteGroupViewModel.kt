package com.seabird.whatsdev.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.db.GroupRepository
import com.seabird.whatsdev.network.model.GroupModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteGroupViewModel  @Inject constructor(
    private val groupRepository: GroupRepository
): ViewModel() {

    var groups = mutableListOf<GroupModel>()
    var notifyNewGroupsInsertedLiveData = MutableLiveData<Pair<Int, Int>>()

    init {
        groups = mutableListOf()
        notifyNewGroupsInsertedLiveData = MutableLiveData<Pair<Int, Int>>()
    }

    fun getGroupList() {
        viewModelScope.launch {
            val groupList = groupRepository.getAllGroups()
            val startIndex = groups.size
            groups.addAll(groupList)
            notifyNewGroupsInsertedLiveData.postValue(Pair(startIndex, groupList.size))
        }
    }

}