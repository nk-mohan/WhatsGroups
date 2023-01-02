package com.seabird.whatsdev.ui.category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.network.model.CategoryModel

class CategoryAdapter(var categoryList: MutableList<CategoryModel>) : RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryData = categoryList[position]
        holder.bindValues(categoryData)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}