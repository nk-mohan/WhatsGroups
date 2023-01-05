package com.seabird.whatsdev.ui.groups

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.isValidIndex
import com.seabird.whatsdev.network.model.GroupModel
import com.seabird.whatsdev.ui.GroupItemClickListener
import com.seabird.whatsdev.utils.AppConstants

class GroupsAdapter(var groupList: MutableList<GroupModel>, var isFromFavorite: Boolean = false, val clickListener: GroupItemClickListener, private val selectedList: ArrayList<Int> = arrayListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoadingAdded = false
    private var loaderPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == AppConstants.ITEM) {
            GroupsViewHolder.create(parent, clickListener)
        } else ProgressViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val groupData = groupList[position]
        when (holder) {
            is GroupsViewHolder -> holder.bindValues(groupData, isFromFavorite, selectedList.contains(groupData.id))
            is ProgressViewHolder -> holder.updateLoader(groupList.size <= 1)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position)
        else {
            val groupData = groupList[position]
            if (holder is GroupsViewHolder)
                holder.updateSelection(selectedList.contains(groupData.id))
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (groupList[position].title.isBlank()) AppConstants.LOADING else AppConstants.ITEM
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

    fun resetAdapter() {
        isLoadingAdded = false
        loaderPosition = -1
        notifyDataSetChanged()
    }
}