package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.databinding.RowStatusItemBinding

class StatusViewHolder(private val viewBinding: RowStatusItemBinding,) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(uri: Uri, position: Int, onItemClicked: (Int) -> Unit) {
        viewBinding.uri = uri

        if (uri.path!!.contains(".jpg")) {
            viewBinding.playLayout.visibility = View.GONE
        } else {
            viewBinding.playLayout.visibility = View.VISIBLE
        }

        viewBinding.playLayout.setOnClickListener {
            onItemClicked(position)
        }

        viewBinding.imageView.setOnClickListener {
            onItemClicked(position)
        }
    }

    companion object {
        fun create(parent: ViewGroup): StatusViewHolder {
            val binding = RowStatusItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return StatusViewHolder(binding)
        }
    }
}