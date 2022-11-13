package com.seabird.whatsdev.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.RowGroupItemBinding
import com.seabird.whatsdev.network.model.GroupData
import com.seabird.whatsdev.setSafeOnClickListener

class GroupsViewHolder(private var viewBinding: RowGroupItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(groupData: GroupData, onItemClicked: (GroupData) -> Unit) {
        viewBinding.groupData = groupData

        viewBinding.favorite.setSafeOnClickListener {
            viewBinding.favorite.setImageResource(R.drawable.ic_favorite)
        }

        viewBinding.rootLayout.setSafeOnClickListener {
            onItemClicked(groupData)
        }
    }

    companion object {
        fun create(parent: ViewGroup): GroupsViewHolder {
            val binding = RowGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return GroupsViewHolder(binding)
        }
    }
}