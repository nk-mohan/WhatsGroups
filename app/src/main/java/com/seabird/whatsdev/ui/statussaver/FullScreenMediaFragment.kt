package com.seabird.whatsdev.ui.statussaver

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.seabird.whatsdev.R
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.FragmentFullScreenMediaBinding
import com.seabird.whatsdev.utils.AppConstants


class FullScreenMediaFragment : Fragment() {

    private var _binding: FragmentFullScreenMediaBinding? = null
    private val binding get() = _binding!!

    private val statusSaverViewModel: StatusSaverViewModel by activityViewModels()

    private lateinit var viewPagerAdapter: ViewPagerAdapter

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

        val mediaList = if (fromImageList) statusSaverViewModel.imageList.value!! else statusSaverViewModel.videoList.value!!
        viewPagerAdapter = ViewPagerAdapter(mediaList)

        binding.viewPager.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            setCurrentItem(selectedMediaPosition, false)
            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        Log.d(TAG, "Position: $position")
                    }
                }
            )
        }

        viewPagerAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putString(AppConstants.VIDEO_URI, it.toString())
            findNavController().navigate(R.id.nav_video_player, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
        _binding = null
    }
}