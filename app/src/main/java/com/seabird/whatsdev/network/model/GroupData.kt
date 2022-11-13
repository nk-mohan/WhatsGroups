package com.seabird.whatsdev.network.model

import android.os.Parcel
import android.os.Parcelable

data class GroupData(val groupName: String, val groupCategory: String, val groupDescription: String, val viewsCount: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupName)
        parcel.writeString(groupCategory)
        parcel.writeString(groupDescription)
        parcel.writeInt(viewsCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupData> {
        override fun createFromParcel(parcel: Parcel): GroupData {
            return GroupData(parcel)
        }

        override fun newArray(size: Int): Array<GroupData?> {
            return arrayOfNulls(size)
        }
    }
}
