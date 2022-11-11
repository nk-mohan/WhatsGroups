package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.databinding.RowStatusItemBinding
import com.seabird.whatsdev.setSafeOnClickListener

class StatusViewHolder(private val viewBinding: RowStatusItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(uri: Uri, position: Int, isSelected: Boolean, onItemClicked: (Int) -> Unit, onItemLongClicked: (Int) -> Unit) {

        viewBinding.uri = uri
        viewBinding.playLayout.visibility = if (uri.path!!.contains(".jpg")) View.GONE else View.VISIBLE
        viewBinding.selectedLayout.visibility = if (isSelected) View.VISIBLE else View.GONE

        viewBinding.playLayout.setSafeOnClickListener {
            onItemClicked(position)
        }

        viewBinding.imageView.setSafeOnClickListener {
            onItemClicked(position)
        }

        viewBinding.imageView.setOnLongClickListener {
            onItemLongClicked(position)
            false
        }

        viewBinding.playLayout.setOnLongClickListener {
            onItemLongClicked(position)
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