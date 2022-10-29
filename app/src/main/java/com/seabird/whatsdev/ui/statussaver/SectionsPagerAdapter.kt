package com.seabird.whatsdev.ui.statussaver

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.seabird.whatsdev.R
import com.seabird.whatsdev.ui.statussaver.PlaceholderFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_image,
    R.string.tab_video
)


class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return PlaceholderFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }
}