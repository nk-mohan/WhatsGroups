package com.seabird.whatsdev.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.db.GroupRepository
import com.seabird.whatsdev.isValidIndex
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
    var notifyNewGroupsDeletedLiveData = MutableLiveData<Pair<Int, Int>>()

    val selectedList = ArrayList<Int>()
    val clearSelection = MutableLiveData<Boolean>()

    init {
        groups = mutableListOf()
        notifyNewGroupsInsertedLiveData = MutableLiveData<Pair<Int, Int>>()
        notifyNewGroupsDeletedLiveData = MutableLiveData<Pair<Int, Int>>()
    }

    fun getGroupList() {
        viewModelScope.launch {
            val groupList = groupRepository.getAllGroups()
            val startIndex = groups.size
            groups.addAll(groupList)
            selectedList.clear()
            notifyNewGroupsInsertedLiveData.postValue(Pair(startIndex, groupList.size))
        }
    }

    fun resetFavouriteItems() {
        if (groups.isNotEmpty()) {
            val size = groups.size
            groups = mutableListOf()
            notifyNewGroupsDeletedLiveData.postValue(Pair(0, size))
        }
    }

    fun selectOrDeselectGroupItem(position: Int) {
        if (groups.size > position) {
            val groupModel = groups[position]
            if (selectedList.contains(groupModel.id))
                selectedList.remove(groupModel.id)
            else
                selectedList.add(groupModel.id)
        }
    }


    fun deleteSelectedItems() {
        viewModelScope.launch {
            if (selectedList.isNotEmpty()) {
                val iterator = selectedList.iterator()
                while (iterator.hasNext()) {
                    val id = iterator.next()
                    groupRepository.deleteGroup(id)
                    val index = groups.indexOfFirst { it.id == id }
                    if (index.isValidIndex()) {
                        val groupModel = groups.first { it.id == id }
                        groups.remove(groupModel)
                        notifyNewGroupsDeletedLiveData.postValue(Pair(index, 1))
                    }
                }
            }
        }
    }

    fun clearAllData() {
        selectedList.clear()
        clearSelection.value = true
    }

}