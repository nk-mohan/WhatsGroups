package com.seabird.whatsdev.ui.statussaver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.FragmentStatusVideoBinding

class StatusVideoFragment : Fragment() {

    private var _binding: FragmentStatusVideoBinding? = null
    private val binding get() = _binding!!

    private val statusSaverViewModel: StatusSaverViewModel by activityViewModels()

    private val statusAdapter: StatusAdapter by lazy { StatusAdapter() }

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
        binding.rvStatusVideo.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            setEmptyView(binding.emptyView.emptyViewLayout)
            adapter = statusAdapter
            addItemDecoration(GridSpacingItemDecoration(requireContext(), 3))
        }

        binding.emptyView.viewStatus.setOnClickListener {
            val launchIntent: Intent? = requireActivity().packageManager.getLaunchIntentForPackage("com.whatsapp")
            if (launchIntent != null) {
                startActivity(launchIntent)
            }
        }
    }


    private fun setObservers() {
        statusSaverViewModel.videoList.observe(viewLifecycleOwner) {
            statusAdapter.setMedias(it)
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