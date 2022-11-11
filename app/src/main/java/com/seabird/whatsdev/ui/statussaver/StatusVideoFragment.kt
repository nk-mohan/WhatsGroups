package com.seabird.whatsdev.ui.statussaver

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentStatusVideoBinding
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.utils.AppConstants

class StatusVideoFragment : Fragment() {

    private var _binding: FragmentStatusVideoBinding? = null
    private val binding get() = _binding!!

    private val statusSaverViewModel: StatusSaverViewModel by activityViewModels()

    private val statusAdapter: StatusAdapter by lazy { StatusAdapter(statusSaverViewModel.selectedList) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setObservers()
    }

    private fun setUpViews() {
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE

        binding.rvStatusVideo.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            setEmptyView(null)
            adapter = statusAdapter
            addItemDecoration(GridSpacingItemDecoration(requireContext(), 3))
        }

        binding.emptyView.viewStatus.setSafeOnClickListener {
            val launchIntent: Intent? = requireActivity().packageManager.getLaunchIntentForPackage("com.whatsapp")
            if (launchIntent != null) {
                startActivity(launchIntent)
            }
        }
    }


    private fun setObservers() {
        statusSaverViewModel.videoList.observe(viewLifecycleOwner) {
            it?.let {
                binding.rvStatusVideo.setEmptyView(binding.emptyView.emptyViewLayout)
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                statusAdapter.setMedias(it)
            }

            statusSaverViewModel.clearSelection.observe(viewLifecycleOwner) {
                if (it && statusSaverViewModel.videoList.value != null) {
                    val bundle = Bundle()
                    bundle.putInt(AppConstants.REFRESH_SELECTION, 1)
                    statusAdapter.notifyItemRangeChanged(0, statusSaverViewModel.videoList.value!!.size, bundle)
                }
            }
        }

        statusAdapter.setVideoItemClickListener {
            if (statusSaverViewModel.selectedList.isEmpty()) {
                val bundle = Bundle()
                bundle.putInt(AppConstants.MEDIA_POSITION, it)
                bundle.putBoolean(AppConstants.FROM_IMAGE_LIST, false)
                findNavController().navigate(R.id.nav_status_viewer, bundle)
            } else {
                statusSaverViewModel.selectOrDeselectVideoItem(it)
                statusAdapter.notifyItemChanged(it)
                (activity as MainActivity).onStatusItemSelected()
            }
        }

        statusAdapter.setVideoItemLongClickListener {
            statusSaverViewModel.selectOrDeselectVideoItem(it)
            statusAdapter.notifyItemChanged(it)
            (activity as MainActivity).onStatusItemSelected()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = StatusVideoFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}