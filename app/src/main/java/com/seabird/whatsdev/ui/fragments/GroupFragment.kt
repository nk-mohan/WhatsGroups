package com.seabird.whatsdev.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.FragmentGroupBinding
import com.seabird.whatsdev.isInternetAvailable
import com.seabird.whatsdev.network.model.GroupModel
import com.seabird.whatsdev.network.other.Status
import com.seabird.whatsdev.ui.GroupItemClickListener
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.ui.adapters.GroupsAdapter
import com.seabird.whatsdev.ui.views.PaginationScrollListener
import com.seabird.whatsdev.ui.views.CustomRecyclerView
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.viewmodels.GroupViewModel

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val groupViewModel: GroupViewModel by activityViewModels()

    private val groupItemClickListener = object : GroupItemClickListener {
        override fun onGroupItemClicked(position: Int, groupModel: GroupModel) {
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.GROUP_DATA, groupModel)
            findNavController().navigate(R.id.nav_view_group, bundle)
        }

        override fun onGroupItemLongClicked(position: Int) {
            //Not needed
        }

        override fun onFavouriteItemClicked(position: Int, groupModel: GroupModel) {
            groupViewModel.updateFavouriteItem(groupModel)
            groupsAdapter.notifyItemChanged(position)
        }

        override fun isFavouriteItem(groupModel: GroupModel): Boolean {
            return groupViewModel.isFavouriteItem(groupModel)
        }
    }

    private val groupsAdapter: GroupsAdapter by lazy { GroupsAdapter(mutableListOf(), clickListener = groupItemClickListener) }
    private val groupsSearchAdapter: GroupsAdapter by lazy { GroupsAdapter(mutableListOf(), clickListener = groupItemClickListener) }

    private var searchString = AppConstants.EMPTY_STRING
    private var mGroupType = GroupType.GROUPS

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
        groupsSearchAdapter.groupList = groupViewModel.searchGroups
        (activity as MainActivity).showSearchGroup()
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

        groupViewModel.notifySearchGroupsInsertedLiveData.observe(viewLifecycleOwner) {
            groupsSearchAdapter.notifyItemRangeInserted(it.first, it.second)
        }

        groupViewModel.addSearchLoader.observe(viewLifecycleOwner) {
            if (it) {
                groupsSearchAdapter.addLoadingFooter()
                binding.rvGroupList.setEmptyView(binding.emptyList.textEmptyView)
            }
        }

        groupViewModel.removeSearchLoader.observe(viewLifecycleOwner) {
            if (it)
                groupsSearchAdapter.removeLoadingFooter()
        }

        groupViewModel.fetchingSearchError.observe(viewLifecycleOwner) {
            if (it) {
                showLoadGroupsError()
            }
        }

        binding.emptyList.retry.setOnClickListener {
            if (requireContext().isInternetAvailable()) {
                groupViewModel.addLoaderToTheList()
                groupViewModel.getGroupList()
            } else
                Toast.makeText(requireContext(), getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if(searchString.isBlank()) {
                groupViewModel.resetResult()
                groupsAdapter.resetAdapter()
                groupViewModel.addLoaderToTheList()
                groupViewModel.getGroupList()
            }
            binding.swipeRefreshLayout.isRefreshing = false
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

        groupViewModel.searchKeyLiveData.observe(viewLifecycleOwner) {
            searchString = it
            groupViewModel.resetSearchResult()
            groupsSearchAdapter.resetAdapter()
            if (it.isNullOrBlank()) {
                mGroupType = GroupType.GROUPS
            } else {
                mGroupType = GroupType.SEARCH
                groupViewModel.addSearchLoaderToTheList()
                groupViewModel.getSearchGroupList(it)
            }
            setAdapterBasedOnSearchType()
        }
    }

    private fun showLoadGroupsError() {
        if (requireContext().isInternetAvailable()) {
            binding.emptyList.textContent.text = getString(R.string.group_list_not_loaded)
            binding.emptyList.imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_group_list_failed))
        } else {
            binding.emptyList.textContent.text = getString(R.string.internet_not_available)
            binding.emptyList.imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_internet_not_available))
            if ((searchString.isBlank() && groupViewModel.groups.size > 1) || (searchString.isNotBlank() && groupViewModel.searchGroups.size > 1))
                Toast.makeText(requireContext(), getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show()
        }
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
                if (searchString.isBlank())
                    groupViewModel.getGroupList()
                else
                    groupViewModel.getSearchGroupList(searchString)
            }

            override fun isLastPage(): Boolean {
                return if (searchString.isBlank()) groupViewModel.lastPageFetched() else groupViewModel.lastSearchPageFetched()
            }

            override fun isFetching(): Boolean {
                return if (searchString.isBlank()) groupViewModel.getGroupsFetching() else groupViewModel.getSearchGroupFetching()
            }
        })
    }

    private fun setAdapterBasedOnSearchType() {
        if (mGroupType == GroupType.GROUPS) {
            binding.rvGroupList.adapter = groupsAdapter
        } else if (mGroupType == GroupType.SEARCH) {
            binding.rvGroupList.adapter = groupsSearchAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).hideSearchGroup()
        searchString = AppConstants.EMPTY_STRING
        mGroupType = GroupType.GROUPS
        _binding = null
    }
}

enum class GroupType {
    GROUPS,
    SEARCH
}