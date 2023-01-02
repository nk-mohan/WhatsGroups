package com.seabird.whatsdev.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.databinding.RowCategoryItemBinding
import com.seabird.whatsdev.network.model.CategoryModel
import com.seabird.whatsdev.setSafeOnClickListener

class CategoryViewHolder(private val viewBinding: RowCategoryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bindValues(categoryData: CategoryModel, onItemClicked: (CategoryModel) -> Unit) {
        viewBinding.categoryData = categoryData

        viewBinding.rootLayout.setSafeOnClickListener {
            onItemClicked(categoryData)
        }
    }

    companion object {
        fun create(parent: ViewGroup): CategoryViewHolder {
            val binding = RowCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CategoryViewHolder(binding)
        }
    }
}