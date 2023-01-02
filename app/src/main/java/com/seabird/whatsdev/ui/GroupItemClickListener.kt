package com.seabird.whatsdev.ui

import com.seabird.whatsdev.network.model.GroupModel

interface GroupItemClickListener {
    fun onGroupItemClicked(groupModel: GroupModel)
    fun onFavouriteItemClicked(position: Int, groupModel: GroupModel)
    fun isFavouriteItem(groupModel: GroupModel) : Boolean
}