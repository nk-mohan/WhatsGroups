package com.seabird.whatsdev.ui.category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.network.model.CategoryData

class CategoryAdapter : RecyclerView.Adapter<CategoryViewHolder>() {

    private val categoryList = arrayListOf<CategoryData>()

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

    fun setCategories(categoryData: List<CategoryData>){
        categoryList.clear()
        categoryList.addAll(categoryData)
        notifyDataSetChanged()
    }
}