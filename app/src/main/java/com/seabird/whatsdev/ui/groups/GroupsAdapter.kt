package com.seabird.whatsdev.ui.groups

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.isValidIndex
import com.seabird.whatsdev.network.model.GroupModel
import com.seabird.whatsdev.utils.AppConstants

class GroupsAdapter(var groupList: MutableList<GroupModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoadingAdded = false
    private var loaderPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == AppConstants.ITEM) {
            GroupsViewHolder.create(parent)
        } else ProgressViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val groupData = groupList[position]
        when (holder) {
            is GroupsViewHolder -> holder.bindValues(groupData, onItemClicked)
            is ProgressViewHolder -> holder.updateLoader(groupList.size <= 1)
        }

    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (groupList[position].title.isBlank()) AppConstants.LOADING else AppConstants.ITEM
    }

    fun setItemClickListener(fn: (GroupModel) -> Unit) {
        onItemClicked = fn
    }

    fun addLoadingFooter() {
        if (!isLoadingAdded) {
            isLoadingAdded = true
            groupList.add(GroupModel())
            loaderPosition = groupList.size - 1
            notifyItemInserted(loaderPosition)
        }
    }

    fun removeLoadingFooter() {
        if (isLoadingAdded) {
            isLoadingAdded = false
            if (loaderPosition.isValidIndex() && groupList.size > loaderPosition) {
                groupList.removeAt(loaderPosition)
                notifyItemRemoved(loaderPosition)
                loaderPosition = -1
            }
        }
    }

    companion object {
        lateinit var onItemClicked: (GroupModel) -> Unit
    }
}