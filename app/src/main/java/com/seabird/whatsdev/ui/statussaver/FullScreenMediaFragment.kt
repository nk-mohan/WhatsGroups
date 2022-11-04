package com.seabird.whatsdev.ui.statussaver

import android.Manifest
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.seabird.whatsdev.R
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.FragmentFullScreenMediaBinding
import com.seabird.whatsdev.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class FullScreenMediaFragment : Fragment() {

    private var _binding: FragmentFullScreenMediaBinding? = null
    private val binding get() = _binding!!

    private val statusSaverViewModel: StatusSaverViewModel by activityViewModels()

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    var selectedMediaPosition = 0
    var fromImageList = false

    lateinit var mediaList:ArrayList<Uri>

    private var lastClickAction = 0

    @Inject
    lateinit var permissionAlertDialog: PermissionAlertDialog

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: PermissionManager.isReadFilePermissionAllowed(requireContext())
        if (readPermissionGranted) {
            performClickAction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        _binding = FragmentFullScreenMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        selectedMediaPosition = arguments?.getInt(AppConstants.MEDIA_POSITION, 0) ?: 0
        fromImageList = arguments?.getBoolean(AppConstants.FROM_IMAGE_LIST, true) ?: true

        mediaList = if (fromImageList) statusSaverViewModel.imageList.value!! else statusSaverViewModel.videoList.value!!
        viewPagerAdapter = ViewPagerAdapter(mediaList)

        binding.backView.text = if (fromImageList) getString(R.string.tab_image) else getString(R.string.tab_video)

        binding.viewPager.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            setCurrentItem(selectedMediaPosition, false)
            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        selectedMediaPosition = position
                    }
                }
            )
        }

        viewPagerAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putString(AppConstants.VIDEO_URI, it.toString())
            findNavController().navigate(R.id.nav_video_player, bundle)
        }

        binding.downloadStatus.setOnClickListener {
            lastClickAction = 0
            checkWritePermission()
        }

        binding.shareStatus.setOnClickListener {
            lastClickAction = 1
            checkWritePermission()
        }

        binding.repostStatus.setOnClickListener {
            lastClickAction = 2
            checkWritePermission()

        }

        binding.backView.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
        _binding = null
    }

    private fun checkWritePermission() {
        if (PermissionManager.isReadFilePermissionAllowed(requireContext())
            && PermissionManager.isWriteFilePermissionAllowed(requireContext())) {
            performClickAction()
        } else {
            PermissionManager.requestStoragePermission(requireActivity(), permissionAlertDialog, storagePermissionLauncher, object :
                PermissionDialogListener {
                override fun onPositiveButtonClicked() {
                    //Not Needed
                }

                override fun onNegativeButtonClicked() {
                    // Not needed
                }
            })
        }
    }

    private fun performClickAction() {
        when(lastClickAction) {
            0 -> {
                downloadStatus()?.let {
                    Snackbar.make(binding.rootLayout, getString(R.string.status_saved), Snackbar.LENGTH_LONG).show()
                    val mimeType = MimeTypeMap.getFileExtensionFromUrl(it.absolutePath)
                    MediaScannerConnection.scanFile(requireContext(), arrayOf(it.absolutePath), arrayOf(mimeType), null)
                }
            }
            1 -> {
                shareStatus()
            }
            2 -> {
                repostStatus()
            }
        }
    }

    private fun downloadStatus(): File? {
        try {
            val filename = File(mediaList[selectedMediaPosition].path).name
            val downloadFile = AppUtils.getDownloadedFile(requireContext(), filename)
            val inputStream = requireActivity().contentResolver.openInputStream(mediaList[selectedMediaPosition])
            val fileOutputStream = FileOutputStream(downloadFile)
            inputStream?.let {
                DownloadUtils.copyStream(inputStream, fileOutputStream)
                fileOutputStream.close()
                inputStream.close()
                return downloadFile
            }
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage)
        }
        return null
    }

    private fun shareStatus() {
        downloadStatus()?.let {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "*/*"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_status_desc))
            val uri = Uri.parse(it.absolutePath)
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_status_desc)))
        }
    }

    private fun repostStatus() {
        downloadStatus()?.let {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "*/*"
            val uri = Uri.parse(it.absolutePath)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_status_desc));
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
            sharingIntent.setPackage("com.whatsapp")
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_status_desc)))
        }
    }

}