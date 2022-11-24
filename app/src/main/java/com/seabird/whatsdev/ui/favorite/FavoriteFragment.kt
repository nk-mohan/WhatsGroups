package com.seabird.whatsdev.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seabird.whatsdev.databinding.FragmentFavoriteBinding
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.ui.groups.GroupViewModel
import com.seabird.whatsdev.ui.groups.GroupsAdapter

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    private val binding get() = _binding!!

    private val groupViewModel: GroupViewModel by activityViewModels()

    private val groupsAdapter: GroupsAdapter by lazy { GroupsAdapter(mutableListOf()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        (activity as MainActivity).showAddGroupAction()
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setObservers()
        groupsAdapter.groupList = groupViewModel.groups
        groupViewModel.getGroupList()
    }

    private fun setObservers() {
        groupViewModel.notifyNewGroupInsertedLiveData.observe(viewLifecycleOwner) {
            groupsAdapter.notifyItemInserted(it)
        }
    }

    private fun initViews() {
        binding.emptyList.textEmptyView.text = "Group list not loaded"
        binding.rvGroupList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false).apply {
                isSmoothScrollbarEnabled = true
            }
            adapter = groupsAdapter
            setEmptyView(binding.emptyList.textEmptyView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}