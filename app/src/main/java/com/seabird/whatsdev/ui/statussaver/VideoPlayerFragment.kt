package com.seabird.whatsdev.ui.statussaver

import android.widget.MediaController
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.seabird.whatsdev.databinding.FragmentVideoPlayerBinding
import com.seabird.whatsdev.utils.AppConstants


class VideoPlayerFragment : Fragment() {

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private val mediaController by lazy { MediaController(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val videoUri = arguments?.getString(AppConstants.VIDEO_URI, "")!!.toUri()

        mediaController.apply {
            setAnchorView(binding.videoView)
            setMediaPlayer(binding.videoView)
        }

        binding.videoView.setOnPreparedListener {
            binding.videoView.setMediaController(mediaController)
        }

        binding.videoView.apply {
            setVideoURI(videoUri)
            start()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}