package com.seabird.whatsdev.ui.addgroup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentAddGroupBinding
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.MainActivity

class AddGroupFragment : Fragment() {

    private var _binding: FragmentAddGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
            Toast.makeText(requireContext(), getString(R.string.group_added), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}