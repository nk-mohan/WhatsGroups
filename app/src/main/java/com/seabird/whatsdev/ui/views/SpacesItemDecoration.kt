package com.seabird.whatsdev.ui.views

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.seabird.whatsdev.R

class SpacesItemDecoration(context: Context, private val spanCount: Int) : ItemDecoration() {

    private val spacing: Int =
        context.resources.getDimensionPixelSize(R.dimen.dimen_10)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)

        val column: Int = itemPosition % spanCount // item column

        if (column == 0) {
            outRect.left = 2 * spacing / spanCount // first item left
            outRect.right = spacing / spanCount // first item right
        } else {
            outRect.left = spacing / spanCount // second item left
            outRect.right = 2 * spacing / spanCount // second item right
        }
        outRect.top = spacing // item top
    }

}