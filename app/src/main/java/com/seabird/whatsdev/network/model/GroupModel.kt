package com.seabird.whatsdev.network.model

import android.os.Parcel
import android.os.Parcelable
import com.seabird.whatsdev.utils.AppConstants

data class GroupModel(val id: Int, val title: String, val category: String, val description: String, val created_at: String, val link: String, val views_count: Int, val report_count: Int) :
    Parcelable {

    constructor() : this(
        0, AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING,AppConstants.EMPTY_STRING,AppConstants.EMPTY_STRING,AppConstants.EMPTY_STRING, 0, 0
    )
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(created_at)
        parcel.writeString(link)
        parcel.writeInt(views_count)
        parcel.writeInt(report_count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupModel> {
        override fun createFromParcel(parcel: Parcel): GroupModel {
            return GroupModel(parcel)
        }

        override fun newArray(size: Int): Array<GroupModel?> {
            return arrayOfNulls(size)
        }
    }
}
