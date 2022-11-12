package com.seabird.whatsdev.ui.trending

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentFavoriteBinding
import com.seabird.whatsdev.databinding.FragmentTrendingBinding
import com.seabird.whatsdev.ui.groups.GroupViewModel
import com.seabird.whatsdev.ui.groups.GroupsAdapter

class TrendingFragment : Fragment() {

    private var _binding: FragmentTrendingBinding? = null

    private val binding get() = _binding!!

    private val groupViewModel by viewModels<GroupViewModel>()

    private val groupsAdapter: GroupsAdapter by lazy { GroupsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        _binding = FragmentTrendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setObservers()
        groupViewModel.getGroupList()
    }

    private fun setObservers() {
        groupViewModel.groupList.observe(viewLifecycleOwner) {
            groupsAdapter.setGroupList(it)
        }
    }

    private fun initViews() {
        binding.emptyList.textEmptyView.text = "Group list not loaded"
        binding.rvGroupList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupsAdapter
            setEmptyView(binding.emptyList.textEmptyView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}