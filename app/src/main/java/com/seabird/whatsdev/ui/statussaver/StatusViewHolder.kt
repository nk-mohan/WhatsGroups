package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.databinding.RowStatusItemBinding

class StatusViewHolder(private val viewBinding: RowStatusItemBinding, val onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(uri: Uri, position: Int) {
        viewBinding.uri = uri

        viewBinding.imageView.setOnClickListener {
            onItemClicked(position)
        }
    }

    companion object {
        fun create(parent: ViewGroup, onItemClicked: (Int) -> Unit): StatusViewHolder {
            val binding = RowStatusItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return StatusViewHolder(binding, onItemClicked)
        }
    }
}