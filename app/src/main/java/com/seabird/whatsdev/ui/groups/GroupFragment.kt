package com.seabird.whatsdev.ui.groups

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.FragmentGroupBinding
import com.seabird.whatsdev.network.other.Status
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.SharedPreferenceManager

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
        groupViewModel.notifyNewGroupInsertedLiveData.observe(viewLifecycleOwner) {
            groupsAdapter.notifyItemInserted(it)
        }

        groupsAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.GROUP_DATA, it)
            findNavController().navigate(R.id.nav_view_group, bundle)
        }

        groupViewModel.registerRes.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                }
                Status.ERROR -> {
                    Log.d(TAG, "okhttp Error")
                }
                Status.LOADING -> {
                    Log.d(TAG, "okhttp LOADING")
                }
            }
        }
    }

    private fun initViews() {
        (activity as MainActivity).showAddGroupAction()
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