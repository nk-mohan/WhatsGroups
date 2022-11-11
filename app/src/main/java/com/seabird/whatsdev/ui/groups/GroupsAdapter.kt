package com.seabird.whatsdev.ui.groups

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.network.model.GroupData

class GroupsAdapter : RecyclerView.Adapter<GroupsViewHolder>() {

    private val groupList = arrayListOf<GroupData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        return GroupsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val groupData = groupList[position]
        holder.bindValues(groupData)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    fun setGroupList(groupData: List<GroupData>) {
        groupList.clear()
        groupList.addAll(groupData)
        notifyDataSetChanged()
    }
}