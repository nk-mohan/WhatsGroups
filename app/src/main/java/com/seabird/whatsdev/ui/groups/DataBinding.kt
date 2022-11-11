package com.seabird.whatsdev.ui.groups

import android.net.Uri
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object DataBinding {

    @JvmStatic
    @BindingAdapter("loadImageInGlide")
    fun loadImage(imageView: AppCompatImageView, url: String) {
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(imageView.context).load(url).apply(requestOptions).into(imageView)
    }

    @JvmStatic
    @BindingAdapter("loadImageInGlideViaUri")
    fun loadImageViaUri(imageView: AppCompatImageView, uri: Uri) {
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(imageView.context).load(uri).apply(requestOptions).into(imageView)
    }
}