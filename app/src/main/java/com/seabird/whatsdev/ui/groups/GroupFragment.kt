package com.seabird.whatsdev.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seabird.whatsdev.databinding.FragmentGroupBinding
import com.seabird.whatsdev.ui.MainActivity

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val groupViewModel:GroupViewModel by activityViewModels()

    private val groupsAdapter: GroupsAdapter by lazy { GroupsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
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
        (activity as MainActivity).showAddGroupAction()
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