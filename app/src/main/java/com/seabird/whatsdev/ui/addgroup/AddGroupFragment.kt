package com.seabird.whatsdev.ui.addgroup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.seabird.whatsdev.R
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.FragmentAddGroupBinding
import com.seabird.whatsdev.network.model.AddGroupRequest
import com.seabird.whatsdev.network.other.Status
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.MainActivity

class AddGroupFragment : Fragment() {

    private var _binding: FragmentAddGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val addGroupViewModel: AddGroupViewModel by activityViewModels()

    private var selectedPosition = 0

    private val categories: List<String> by lazy { resources.getStringArray(R.array.categories).toList() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        (activity as MainActivity).hideAddGroupAction()
        _binding = FragmentAddGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSpinnerView()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.addGroup.setSafeOnClickListener {
            validateAndCreateGroup()
        }

        addGroupViewModel.addGroupRes.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), getString(R.string.group_added), Toast.LENGTH_SHORT).show()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    Log.d(TAG, "okhttp LOADING")
                }
            }
        }
    }

    private fun setUpSpinnerView() {
        binding.categorySpinner.setItems(categories)
        binding.categorySpinner.setOnItemSelectedListener { _, position, _, _ ->
            selectedPosition = position
        }
    }

    private fun validateAndCreateGroup() {
        if (selectedPosition == 0) {
            Toast.makeText(requireContext(), getString(R.string.validation_category), Toast.LENGTH_SHORT).show()
        } else if (binding.groupNameEditText.text.toString().isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.validation_group_name), Toast.LENGTH_SHORT).show()
        } else if (binding.groupLinkEditText.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.validation_group_link), Toast.LENGTH_SHORT).show()
        } else if (binding.groupDescriptionEditText.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.validation_group_description), Toast.LENGTH_SHORT).show()
        } else {
            addGroupViewModel.addGroup(AddGroupRequest(
                binding.groupNameEditText.text.toString(),
                binding.groupDescriptionEditText.text.toString(),
                binding.groupLinkEditText.text.toString(),
                categories[selectedPosition]
            ))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}