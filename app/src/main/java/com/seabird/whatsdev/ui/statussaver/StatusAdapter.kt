package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.ui.StatusItemClickListener

class StatusAdapter(
    private val selectedList: ArrayList<Uri>,
    private val statusItemClickListener: StatusItemClickListener
) : RecyclerView.Adapter<StatusViewHolder>() {

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
            statusItemClickListener
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
}