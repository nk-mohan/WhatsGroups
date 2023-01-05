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

class GroupsViewHolder(private var viewBinding: RowGroupItemBinding, private val groupItemClickListener: GroupItemClickListener) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(groupData: GroupModel, isFromFavorite: Boolean, isSelected: Boolean) {
        viewBinding.groupData = groupData

        viewBinding.favorite.visibility = if (isFromFavorite) View.GONE else View.VISIBLE
        updateSelection(isSelected)

        viewBinding.favorite.setImageResource(if (groupItemClickListener.isFavouriteItem(groupData)) R.drawable.ic_favorite else R.drawable.ic_not_favorite)

        viewBinding.favorite.setSafeOnClickListener {
            groupItemClickListener.onFavouriteItemClicked(layoutPosition, groupData)
        }

        viewBinding.rootLayout.setSafeOnClickListener {
            groupItemClickListener.onGroupItemClicked(layoutPosition,groupData)
        }

        viewBinding.rootLayout.setOnLongClickListener {
            groupItemClickListener.onGroupItemLongClicked(layoutPosition)
            false
        }

        viewBinding.selectedLayout.setSafeOnClickListener {
            groupItemClickListener.onGroupItemClicked(layoutPosition, groupData)
        }
    }

    fun updateSelection(isSelected: Boolean) {
        viewBinding.selectedLayout.visibility = if (isSelected) View.VISIBLE else View.GONE
    }

    companion object {
        fun create(parent: ViewGroup, groupItemClickListener: GroupItemClickListener): GroupsViewHolder {
            val binding = RowGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return GroupsViewHolder(binding, groupItemClickListener)
        }
    }
}