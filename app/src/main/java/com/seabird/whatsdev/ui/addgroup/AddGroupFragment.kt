package com.seabird.whatsdev.ui.addgroup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentAddGroupBinding
import com.seabird.whatsdev.network.model.AddGroupRequest
import com.seabird.whatsdev.network.other.Status
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.AppUtils

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
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.group_added), Toast.LENGTH_SHORT).show()
                    clearData()
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    if (it.code == 409) {
                        Toast.makeText(requireContext(), getString(R.string.group_link_already_exists), Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(requireContext(), AppUtils.getErrorCode(it.code, it.message, requireContext()), Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun clearData() {
        binding.categorySpinner.selectedIndex = 0
        selectedPosition = 0
        binding.groupNameEditText.setText(AppConstants.EMPTY_STRING)
        binding.groupLinkEditText.setText(AppConstants.EMPTY_STRING)
        binding.groupDescriptionEditText.setText(AppConstants.EMPTY_STRING)
    }

    private fun setUpSpinnerView() {
        binding.categorySpinner.setItems(categories)
        binding.categorySpinner.setOnItemSelectedListener { _, position, _, _ ->
            selectedPosition = position
        }
    }

    private fun validateAndCreateGroup() {
        AppUtils.closeKeyboard(requireActivity())
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