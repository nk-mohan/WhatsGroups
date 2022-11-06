package com.seabird.whatsdev.ui.statussaver

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentStatusImageBinding
import com.seabird.whatsdev.utils.AppConstants


class StatusImageFragment : Fragment() {

    private var _binding: FragmentStatusImageBinding? = null
    private val binding get() = _binding!!

    private val statusSaverViewModel: StatusSaverViewModel by activityViewModels()

    private val statusAdapter: StatusAdapter by lazy { StatusAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setObservers()
    }

    private fun setUpViews() {
        binding.shimmerLayout.startShimmer()

        binding.rvStatusImage.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            setEmptyView(null)
            adapter = statusAdapter
            addItemDecoration(GridSpacingItemDecoration(requireContext(), 3))
        }

        binding.emptyView.viewStatus.setOnClickListener {
            val launchIntent: Intent? = requireActivity().packageManager.getLaunchIntentForPackage("com.whatsapp")
            if (launchIntent != null) {
                startActivity(launchIntent)
            }
        }

        statusAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putInt(AppConstants.MEDIA_POSITION, it)
            bundle.putBoolean(AppConstants.FROM_IMAGE_LIST, true)
            findNavController().navigate(R.id.nav_status_viewer, bundle)
        }
    }

    private fun setObservers() {
        statusSaverViewModel.imageList.observe(viewLifecycleOwner) {
            binding.rvStatusImage.setEmptyView(binding.emptyView.emptyViewLayout)
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            statusAdapter.setMedias(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): StatusImageFragment {
            return StatusImageFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}