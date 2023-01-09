package com.seabird.whatsdev.ui.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.seabird.whatsdev.R
import com.seabird.whatsdev.ui.fragments.StatusImageFragment
import com.seabird.whatsdev.ui.fragments.StatusVideoFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_image,
    R.string.tab_video
)

class SectionsPagerAdapter(private val context: Context, activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            StatusImageFragment.newInstance()
        else StatusVideoFragment.newInstance()
    }

    fun getTabTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITLES[position])
    }
}