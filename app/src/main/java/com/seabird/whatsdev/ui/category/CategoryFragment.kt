package com.seabird.whatsdev.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentCategoryBinding
import com.seabird.whatsdev.network.other.Status
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.ui.views.SpacesItemDecoration
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.AppUtils

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val categoryViewModel: CategoryViewModel by activityViewModels()

    private val categoryAdapter by lazy { CategoryAdapter(mutableListOf()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        (activity as MainActivity).showAddGroupAction()
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setObservers()
        categoryAdapter.categoryList = categoryViewModel.categorys
        if (categoryViewModel.categorys.isNotEmpty()) {
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
        } else
            categoryViewModel.getCategoryList()
    }

    private fun setObservers() {
        categoryViewModel.notifyNewCategoriesInsertedLiveData.observe(viewLifecycleOwner) {
            categoryAdapter.notifyItemRangeInserted(it.first, it.second)
        }

        categoryViewModel.categoryRes.observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.rvCategoryList.setEmptyView(binding.emptyList.textEmptyView)
                    }
                    Status.ERROR -> {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.rvCategoryList.setEmptyView(binding.emptyList.textEmptyView)
                        Toast.makeText(requireContext(), AppUtils.getErrorCode(it.code, it.message, requireContext()), Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        binding.shimmerLayout.startShimmer()
                        binding.shimmerLayout.visibility = View.VISIBLE
                    }
                }
            }
        }

        categoryAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putString(AppConstants.CATEGORY_NAME, it.category_name)
            findNavController().navigate(R.id.nav_category_group, bundle)
        }
    }

    private fun initViews() {
        binding.emptyList.textContent.text = "Category list not loaded"
        binding.rvCategoryList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                isSmoothScrollbarEnabled = true
            }
            adapter = categoryAdapter
            addItemDecoration(SpacesItemDecoration(requireContext(), 2))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}