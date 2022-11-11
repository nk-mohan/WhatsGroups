package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StatusAdapter(val selectedList: ArrayList<Uri>) : RecyclerView.Adapter<StatusViewHolder>() {

    private val mediaList = arrayListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        return StatusViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val mediaData = mediaList[position]
        holder.bindValues(
            mediaData,
            position,
            selectedList.contains(mediaData),
            if (mediaData.path!!.contains(".jpg")) onItemClicked else onVideoItemClicked,
            if (mediaData.path!!.contains(".jpg")) onItemLongClicked else onVideoItemLongClicked
        )
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position)
        else {
            holder.updateSelection(selectedList.contains(mediaList[position]))
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    fun setMedias(mediaData: List<Uri>){
        mediaList.clear()
        mediaList.addAll(mediaData)
        notifyDataSetChanged()
    }

    fun setItemClickListener(fn: (Int) -> Unit) {
        onItemClicked = fn
    }

    fun setItemLongClickListener(fn: (Int) -> Unit) {
        onItemLongClicked = fn
    }

    fun setVideoItemLongClickListener(fn: (Int) -> Unit) {
        onVideoItemLongClicked = fn
    }

    fun setVideoItemClickListener(fn: (Int) -> Unit) {
        onVideoItemClicked = fn
    }

    companion object {
        lateinit var onItemClicked: (Int) -> Unit
        lateinit var onItemLongClicked: (Int) -> Unit
        lateinit var onVideoItemClicked: (Int) -> Unit
        lateinit var onVideoItemLongClicked: (Int) -> Unit
    }
}