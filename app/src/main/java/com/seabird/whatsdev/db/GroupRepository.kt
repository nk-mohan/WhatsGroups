package com.seabird.whatsdev.db

import com.seabird.whatsdev.convertToGroupDBModel
import com.seabird.whatsdev.convertToGroupModel
import com.seabird.whatsdev.db.dao.GroupDao
import com.seabird.whatsdev.network.model.GroupModel
import javax.inject.Inject

class GroupRepository @Inject constructor(private val groupDao: GroupDao){

    suspend fun insertGroup(groupModel: GroupModel) {
        groupDao.insertGroup(groupModel.convertToGroupDBModel())
    }

    suspend fun deleteGroup(groupId: Int) {
        groupDao.deleteGroup(groupId)
    }

    suspend fun getAllGroups(): ArrayList<GroupModel> {
        val groupList = ArrayList<GroupModel>()
        groupDao.getGroupList().forEach {
            groupList.add(it.convertToGroupModel())
        }
        return groupList
    }

    fun isFavoriteGroup(groupId: Int): Boolean {
       return groupDao.isFavoriteGroup(groupId).isNotEmpty()
    }
}