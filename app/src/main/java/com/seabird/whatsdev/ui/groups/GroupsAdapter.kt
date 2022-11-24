package com.seabird.whatsdev.ui.groups

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.network.model.GroupData

class GroupsAdapter(var groupList: MutableList<GroupData>) : RecyclerView.Adapter<GroupsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        return GroupsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val groupData = groupList[position]
        holder.bindValues(groupData, onItemClicked)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    fun setItemClickListener(fn: (GroupData) -> Unit) {
        onItemClicked = fn
    }

    companion object {
        lateinit var onItemClicked: (GroupData) -> Unit
    }
}