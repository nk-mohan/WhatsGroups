package com.seabird.whatsdev.ui.viewholders

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.databinding.RowStatusItemBinding
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.StatusItemClickListener

class StatusViewHolder(private val viewBinding: RowStatusItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(uri: Uri, position: Int, isSelected: Boolean, statusItemClickListener: StatusItemClickListener) {

        viewBinding.uri = uri
        viewBinding.playLayout.visibility = if (uri.path!!.contains(".jpg")) View.GONE else View.VISIBLE
        viewBinding.selectedLayout.visibility = if (isSelected) View.VISIBLE else View.GONE

        viewBinding.playLayout.setSafeOnClickListener {
            statusItemClickListener.setItemClickListener(position)
        }

        viewBinding.imageView.setSafeOnClickListener {
            statusItemClickListener.setItemClickListener(position)
        }

        viewBinding.imageView.setOnLongClickListener {
            statusItemClickListener.setItemLongClickListener(position)
            false
        }

        viewBinding.playLayout.setOnLongClickListener {
            statusItemClickListener.setItemLongClickListener(position)
            false
        }
    }

    fun updateSelection(isSelected: Boolean) {
        viewBinding.selectedLayout.visibility = if (isSelected) View.VISIBLE else View.GONE
    }

    companion object {
        fun create(parent: ViewGroup): StatusViewHolder {
            val binding = RowStatusItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return StatusViewHolder(binding)
        }
    }
}