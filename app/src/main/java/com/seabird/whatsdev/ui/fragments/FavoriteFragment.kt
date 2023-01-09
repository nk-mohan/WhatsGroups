package com.seabird.whatsdev.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentFavoriteBinding
import com.seabird.whatsdev.network.model.GroupModel
import com.seabird.whatsdev.ui.GroupItemClickListener
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.ui.adapters.GroupsAdapter
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.viewmodels.FavouriteGroupViewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    private val binding get() = _binding!!

    private val groupViewModel: FavouriteGroupViewModel by activityViewModels()

    private val groupItemClickListener = object : GroupItemClickListener {
        override fun onGroupItemClicked(position: Int, groupModel: GroupModel) {
            if (groupViewModel.selectedList.isEmpty()) {
                val bundle = Bundle()
                bundle.putParcelable(AppConstants.GROUP_DATA, groupModel)
                findNavController().navigate(R.id.nav_view_group, bundle)
            } else {
                groupViewModel.selectOrDeselectGroupItem(position)
                groupsAdapter.notifyItemChanged(position)
                (activity as MainActivity).onFavouriteGroupSelected()
            }
        }

        override fun onGroupItemLongClicked(position: Int) {
            groupViewModel.selectOrDeselectGroupItem(position)
            groupsAdapter.notifyItemChanged(position)
            (activity as MainActivity).onFavouriteGroupSelected()
        }

        override fun onFavouriteItemClicked(position: Int, groupModel: GroupModel) {
            if (groupViewModel.selectedList.isEmpty()) {
                groupsAdapter.notifyItemChanged(position)
            } else {
                groupViewModel.selectOrDeselectGroupItem(position)
                groupsAdapter.notifyItemChanged(position)
                (activity as MainActivity).onFavouriteGroupSelected()
            }
        }

        override fun isFavouriteItem(groupModel: GroupModel): Boolean {
            return true
        }
    }

    private val groupsAdapter: GroupsAdapter by lazy { GroupsAdapter(mutableListOf(), true, groupItemClickListener, groupViewModel.selectedList) }

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
        groupViewModel.resetFavouriteItems()
        groupsAdapter.groupList = groupViewModel.groups
        groupViewModel.getGroupList()
    }

    private fun setObservers() {
        groupViewModel.notifyNewGroupsInsertedLiveData.observe(viewLifecycleOwner) {
            groupsAdapter.notifyItemRangeInserted(it.first, it.second)
        }

        groupViewModel.notifyNewGroupsDeletedLiveData.observe(viewLifecycleOwner) {
            groupsAdapter.notifyItemRangeRemoved(it.first, it.second)
        }

        groupViewModel.clearSelection.observe(viewLifecycleOwner) {
            if (it && groupViewModel.groups.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putInt(AppConstants.REFRESH_SELECTION, 1)
                groupsAdapter.notifyItemRangeChanged(0, groupViewModel.groups.size, bundle)
            }
        }
    }

    private fun initViews() {
        binding.emptyList.textContent.text = getString(R.string.favorite_list_not_loaded)
        binding.emptyList.retry.visibility = View.GONE
        binding.emptyList.imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_group_list_failed))
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