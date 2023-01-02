package com.seabird.whatsdev.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.seabird.whatsdev.db.model.GroupDBModel

@Dao
interface GroupDao {
    @Insert
    suspend fun insertGroup(vararg group: GroupDBModel)

    @Query("SELECT * FROM groups")
    suspend fun getGroupList(): List<GroupDBModel>

    @Query("SELECT * FROM groups WHERE id =:groupId")
    fun isFavoriteGroup(groupId: Int): List<GroupDBModel>

    @Query("DELETE FROM groups WHERE id =:groupId")
    suspend fun deleteGroup(groupId: Int)
}