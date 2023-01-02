package com.seabird.whatsdev.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupDBModel(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "link") val link: String,
    @ColumnInfo(name = "views_count") val views_count: Int,
    @ColumnInfo(name = "report_count") val report_count: Int,
    @ColumnInfo(name = "created_at") val created_at: String
) {
    @PrimaryKey(autoGenerate = true)
    var dbId: Int? = null
}