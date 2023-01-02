package com.seabird.whatsdev.ui.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.RowGroupItemBinding
import com.seabird.whatsdev.network.model.GroupModel
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.GroupItemClickListener

class GroupsViewHolder(private var viewBinding: RowGroupItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(groupData: GroupModel, position: Int, isFromFavorite: Boolean, groupItemClickListener: GroupItemClickListener) {
        viewBinding.groupData = groupData

        viewBinding.favorite.visibility = if (isFromFavorite) View.GONE else View.VISIBLE

        viewBinding.favorite.setImageResource(if (groupItemClickListener.isFavouriteItem(groupData)) R.drawable.ic_favorite else R.drawable.ic_not_favorite)

        viewBinding.favorite.setSafeOnClickListener {
            groupItemClickListener.onFavouriteItemClicked(position, groupData)
        }

        viewBinding.rootLayout.setSafeOnClickListener {
            groupItemClickListener.onGroupItemClicked(groupData)
        }
    }

    companion object {
        fun create(parent: ViewGroup): GroupsViewHolder {
            val binding = RowGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return GroupsViewHolder(binding)
        }
    }
}