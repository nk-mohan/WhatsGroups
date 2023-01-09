package com.seabird.whatsdev.ui.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.databinding.RowGroupShimmerItemBinding

class ProgressViewHolder(private var viewBinding: RowGroupShimmerItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun updateLoader(isInitialLoader: Boolean) {
        if (isInitialLoader) {
            viewBinding.item2.rootLayout.visibility = View.VISIBLE
            viewBinding.item3.rootLayout.visibility = View.VISIBLE
            viewBinding.item4.rootLayout.visibility = View.VISIBLE
            viewBinding.item5.rootLayout.visibility = View.VISIBLE
            viewBinding.item6.rootLayout.visibility = View.VISIBLE
            viewBinding.item7.rootLayout.visibility = View.VISIBLE
            viewBinding.item8.rootLayout.visibility = View.VISIBLE
            viewBinding.item9.rootLayout.visibility = View.VISIBLE
        } else {
            viewBinding.item2.rootLayout.visibility = View.GONE
            viewBinding.item3.rootLayout.visibility = View.GONE
            viewBinding.item4.rootLayout.visibility = View.GONE
            viewBinding.item5.rootLayout.visibility = View.GONE
            viewBinding.item6.rootLayout.visibility = View.GONE
            viewBinding.item7.rootLayout.visibility = View.GONE
            viewBinding.item8.rootLayout.visibility = View.GONE
            viewBinding.item9.rootLayout.visibility = View.GONE
        }
        viewBinding.shimmerLayout.startShimmer()
    }

    companion object {
        fun create(parent: ViewGroup): ProgressViewHolder {
            val binding = RowGroupShimmerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProgressViewHolder(binding)
        }
    }
}