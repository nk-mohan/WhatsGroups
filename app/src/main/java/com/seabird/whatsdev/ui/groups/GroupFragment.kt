package com.seabird.whatsdev.ui.groups

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.FragmentGroupBinding
import com.seabird.whatsdev.isInternetAvailable
import com.seabird.whatsdev.network.other.Status
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.ui.views.CustomRecyclerView
import com.seabird.whatsdev.utils.AppConstants

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val groupViewModel: GroupViewModel by activityViewModels()

    private val groupsAdapter: GroupsAdapter by lazy { GroupsAdapter(mutableListOf()) }

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
        groupsAdapter.groupList = groupViewModel.groups
    }

    private fun setObservers() {
        groupViewModel.notifyNewGroupsInsertedLiveData.observe(viewLifecycleOwner) {
            groupsAdapter.notifyItemRangeInserted(it.first, it.second)
        }

        groupViewModel.addLoader.observe(viewLifecycleOwner) {
            if (it) {
                groupsAdapter.addLoadingFooter()
                binding.rvGroupList.setEmptyView(binding.emptyList.textEmptyView)
            }
        }

        groupViewModel.removeLoader.observe(viewLifecycleOwner) {
            if (it)
                groupsAdapter.removeLoadingFooter()
        }

        groupViewModel.fetchingError.observe(viewLifecycleOwner) {
            if (it) {
               showLoadGroupsError()
            }
        }

        groupsAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.GROUP_DATA, it)
            findNavController().navigate(R.id.nav_view_group, bundle)
        }

        binding.emptyList.retry.setOnClickListener {
            if (requireContext().isInternetAvailable()) {
                groupViewModel.addLoaderToTheList()
                groupViewModel.getGroupList()
            } else
                Toast.makeText(requireContext(), getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show()
        }

        groupViewModel.registerRes.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (groupViewModel.groups.isEmpty()) {
                        groupViewModel.addLoaderToTheList()
                        groupViewModel.getGroupList()
                    }
                }
                Status.ERROR -> {
                    Log.d(TAG, "okhttp Error")
                    showLoadGroupsError()
                    binding.rvGroupList.setEmptyView(binding.emptyList.textEmptyView)
                }
                Status.LOADING -> {
                    Log.d(TAG, "okhttp LOADING")
                    binding.rvGroupList.setEmptyView(null)
                }
            }
        }
    }

    private fun showLoadGroupsError() {
        if (requireContext().isInternetAvailable())
            binding.emptyList.textContent.text = getString(R.string.group_list_not_loaded)
        else binding.emptyList.textContent.text = getString(R.string.internet_not_available)

        if (groupViewModel.groups.size > 1)
            Toast.makeText(requireContext(), getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show()
    }

    private fun initViews() {
        (activity as MainActivity).showAddGroupAction()
        binding.emptyList.textContent.text = getString(R.string.group_list_not_loaded)
        binding.rvGroupList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false).apply {
                isSmoothScrollbarEnabled = true
            }
            adapter = groupsAdapter
            setScrollListener(this, layoutManager as LinearLayoutManager)
        }

        binding.emptyList.retry.setOnClickListener {
            if (requireContext().isInternetAvailable())
                groupViewModel.checkAndRegister()
        }
    }

    private fun setScrollListener(
        recyclerView: CustomRecyclerView,
        layoutManager: LinearLayoutManager
    ) {
        recyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager){
            override fun loadMoreItems() {
                groupViewModel.getGroupList()
            }

            override fun isLastPage(): Boolean {
                return groupViewModel.lastPageFetched()
            }

            override fun isFetching(): Boolean {
                return groupViewModel.getUserListFetching()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}