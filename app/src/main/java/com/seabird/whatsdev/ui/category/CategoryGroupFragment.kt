package com.seabird.whatsdev.ui.category

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentCategoryGroupBinding
import com.seabird.whatsdev.isInternetAvailable
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.ui.groups.GroupsAdapter
import com.seabird.whatsdev.ui.groups.PaginationScrollListener
import com.seabird.whatsdev.ui.views.CustomRecyclerView
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.viewmodels.CategoryGroupViewModel


class CategoryGroupFragment : Fragment() {

    private var _binding: FragmentCategoryGroupBinding? = null

    private val binding get() = _binding!!

    private val groupViewModel by activityViewModels<CategoryGroupViewModel>()

    private val groupsAdapter: GroupsAdapter by lazy { GroupsAdapter(mutableListOf()) }

    var categoryName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        _binding = FragmentCategoryGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val argName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getString(AppConstants.CATEGORY_NAME, "") ?: ""
        } else {
            arguments?.getString(AppConstants.CATEGORY_NAME, "") ?: ""
        }
        if (argName != categoryName)
            groupViewModel.resetResult()
        categoryName = argName
        (activity as AppCompatActivity?)?.supportActionBar?.title = categoryName
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
    }

    private fun setScrollListener(
        recyclerView: CustomRecyclerView,
        layoutManager: LinearLayoutManager
    ) {
        recyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager){
            override fun loadMoreItems() {
                groupViewModel.getGroupList(categoryName)
            }

            override fun isLastPage(): Boolean {
                return groupViewModel.lastPageFetched()
            }

            override fun isFetching(): Boolean {
                return groupViewModel.getUserListFetching()
            }
        })
        if (!groupViewModel.lastPageFetched()) {
            groupViewModel.addLoaderToTheList()
            groupViewModel.getGroupList(categoryName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}