package com.seabird.whatsdev.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seabird.whatsdev.db.dao.GroupDao
import com.seabird.whatsdev.db.model.GroupDBModel

@Database(entities = [GroupDBModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupsDao(): GroupDao
}