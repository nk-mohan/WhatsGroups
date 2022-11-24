package com.seabird.whatsdev.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.seabird.whatsdev.databinding.FragmentCategoryBinding
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.ui.views.SpacesItemDecoration

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
        categoryViewModel.getCategoryList()
    }

    private fun setObservers() {
        categoryViewModel.notifyNewCategoryInsertedLiveData.observe(viewLifecycleOwner) {
            categoryAdapter.notifyItemInserted(it)
        }
    }

    private fun initViews() {
        binding.emptyList.textEmptyView.text = "Category list not loaded"
        binding.rvCategoryList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                isSmoothScrollbarEnabled = true
            }
            adapter = categoryAdapter
            addItemDecoration(SpacesItemDecoration(requireContext(), 2))
            setEmptyView(binding.emptyList.textEmptyView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}