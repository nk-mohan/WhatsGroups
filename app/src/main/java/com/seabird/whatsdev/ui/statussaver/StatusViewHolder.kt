package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.databinding.RowStatusItemBinding

class StatusViewHolder(private val viewBinding: RowStatusItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(uri: Uri) {
        viewBinding.uri = uri
    }

    companion object {
        fun create(parent: ViewGroup): StatusViewHolder {
            val binding = RowStatusItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return StatusViewHolder(binding)
        }
    }
}