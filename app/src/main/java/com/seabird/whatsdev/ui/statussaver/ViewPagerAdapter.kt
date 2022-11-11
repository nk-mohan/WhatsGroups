package com.seabird.whatsdev.ui.statussaver

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.seabird.whatsdev.databinding.RowImageItemBinding
import com.seabird.whatsdev.setSafeOnClickListener

class ViewPagerAdapter(private val imageUrlList: List<Uri>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(val binding: RowImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(mediaUri: Uri) {
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(binding.root.context).load(mediaUri).apply(requestOptions).into(binding.imageView)
            if (mediaUri.path!!.contains(".jpg")) {
                binding.playLayout.visibility = View.GONE
                binding.imageView.isZoomable = true
            } else {
                binding.playLayout.visibility = View.VISIBLE
                binding.imageView.isZoomable = false
            }

            binding.playLayout.setSafeOnClickListener {
                onItemClicked(mediaUri)
            }
        }
    }

    fun setItemClickListener(fn: (Uri) -> Unit) {
        onItemClicked = fn
    }

    override fun getItemCount(): Int = imageUrlList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = RowImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(imageUrlList[position])
    }

    companion object {
        lateinit var onItemClicked: (Uri) -> Unit
    }
}