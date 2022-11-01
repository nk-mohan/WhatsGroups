package com.seabird.whatsdev.ui.statussaver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.seabird.whatsdev.databinding.FragmentFullScreenMediaBinding
import com.seabird.whatsdev.utils.AppConstants


class FullScreenMediaFragment : Fragment() {

    private var _binding: FragmentFullScreenMediaBinding? = null
    private val binding get() = _binding!!

    private val statusSaverViewModel: StatusSaverViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        val selectedMediaPosition = arguments?.getInt(AppConstants.MEDIA_POSITION, 0) ?: 0
        val fromImageList = arguments?.getBoolean(AppConstants.FROM_IMAGE_LIST, true) ?: true
        val selectedMedia = if (fromImageList)
            statusSaverViewModel.imageList.value!![selectedMediaPosition]
        else
            statusSaverViewModel.videoList.value!![selectedMediaPosition]
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(requireContext()).load(selectedMedia).apply(requestOptions).into(binding.imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}