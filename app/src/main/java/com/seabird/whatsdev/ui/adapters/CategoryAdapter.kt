package com.seabird.whatsdev.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.network.model.CategoryModel
import com.seabird.whatsdev.ui.viewholders.CategoryViewHolder

class CategoryAdapter(var categoryList: MutableList<CategoryModel>) : RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryData = categoryList[position]
        holder.bindValues(categoryData, onItemClicked)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun setItemClickListener(fn: (CategoryModel) -> Unit) {
        onItemClicked = fn
    }

    companion object {
        lateinit var onItemClicked: (CategoryModel) -> Unit
    }
}