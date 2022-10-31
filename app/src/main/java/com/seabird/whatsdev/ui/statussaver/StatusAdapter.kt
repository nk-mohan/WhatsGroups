package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StatusAdapter : RecyclerView.Adapter<StatusViewHolder>() {

    private val mediaList = arrayListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        return StatusViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val mediaData = mediaList[position]
        holder.bindValues(mediaData)
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    fun setMedias(mediaData: List<Uri>){
        mediaList.clear()
        mediaList.addAll(mediaData)
        notifyDataSetChanged()
    }
}