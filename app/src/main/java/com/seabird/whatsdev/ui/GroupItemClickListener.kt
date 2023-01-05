package com.seabird.whatsdev.ui

import com.seabird.whatsdev.network.model.GroupModel

interface GroupItemClickListener {
    fun onGroupItemClicked(position: Int, groupModel: GroupModel)
    fun onGroupItemLongClicked(position: Int)
    fun onFavouriteItemClicked(position: Int, groupModel: GroupModel)
    fun isFavouriteItem(groupModel: GroupModel) : Boolean
}