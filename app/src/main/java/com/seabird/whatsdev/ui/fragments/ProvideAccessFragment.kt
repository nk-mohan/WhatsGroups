package com.seabird.whatsdev.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seabird.whatsdev.databinding.FragmentProvideAccessBinding
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.SharedPreferenceManager

class ProvideAccessFragment : Fragment() {

    private var _binding: FragmentProvideAccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProvideAccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        if (SharedPreferenceManager.getBooleanValue(AppConstants.GRANT_FOLDER_ACCESS)){
            requireActivity().onBackPressedDispatcher.onBackPressed()
        } else {
            binding.grantFolderAccess.setSafeOnClickListener {
                SharedPreferenceManager.setBooleanValue(AppConstants.GRANT_FOLDER_ACCESS, true)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).refreshStatusList()
        _binding = null
    }
}