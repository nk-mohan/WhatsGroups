package com.seabird.whatsdev.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.databinding.RowGroupItemBinding
import com.seabird.whatsdev.network.model.GroupData

class GroupsViewHolder(private var viewBinding: RowGroupItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(groupData: GroupData) {
        viewBinding.groupData = groupData
    }

    companion object {
        fun create(parent: ViewGroup): GroupsViewHolder {
            val binding = RowGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return GroupsViewHolder(binding)
        }
    }
}