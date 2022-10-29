package com.seabird.whatsdev.ui.statussaver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.seabird.whatsdev.databinding.FragmentStatusSaverBinding

class StatusSaverFragment : Fragment() {

    private var _binding: FragmentStatusSaverBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val statusSaverViewModel by lazy { viewModels<StatusSaverViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusSaverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), requireActivity().supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}