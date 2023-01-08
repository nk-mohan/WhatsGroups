package com.seabird.whatsdev.ui.statussaver

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.seabird.whatsdev.R
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.databinding.FragmentFullScreenMediaBinding
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
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
        (activity as MainActivity).lockNavigationDrawer()
        (activity as MainActivity).hideAddGroupAction()
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

        binding.downloadStatus.setSafeOnClickListener {
            lastClickAction = 0
            checkWritePermission()
        }

        binding.shareStatus.setSafeOnClickListener {
            lastClickAction = 1
            checkWritePermission()
        }

        binding.repostStatus.setSafeOnClickListener {
            lastClickAction = 2
            checkWritePermission()

        }

        binding.backView.setSafeOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
        (activity as MainActivity).unlockNavigationDrawer()
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
            val inputStream = requireActivity().contentResolver.openInputStream(mediaList[selectedMediaPosition])
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                inputStream?.let {
                    val environment = if (filename.contains(".jpg")) Environment.DIRECTORY_PICTURES else Environment.DIRECTORY_MOVIES
                    val mimeType = if (filename.contains(".jpg")) "image/jpg" else "video/mp4"
                    val mediaStoreUri = if (filename.contains(".jpg")) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    val relativeLocation = environment + File.separator + "WhatsApp Status"
                    val file = File(Environment.getExternalStoragePublicDirectory(relativeLocation), filename)
                    return if (!file.exists()) {
                        val path = copyStream(requireContext(), inputStream, filename, environment, mimeType, mediaStoreUri).path
                        if (path != null)
                            File(path)
                        else
                            null
                    } else
                        file
                }
            } else {
                val downloadFile = AppUtils.getDownloadedFile(requireContext(), filename)
                val fileOutputStream = FileOutputStream(downloadFile)
                inputStream?.let {
                    DownloadUtils.copyStream(inputStream, fileOutputStream)
                    fileOutputStream.close()
                    inputStream.close()
                    val mimeType = MimeTypeMap.getFileExtensionFromUrl(downloadFile.absolutePath)
                    MediaScannerConnection.scanFile(requireContext(), arrayOf(downloadFile.absolutePath), arrayOf(mimeType), null)
                    return downloadFile
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage)
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Throws(IOException::class)
    fun copyStream(context: Context, inputStream: InputStream, displayName: String, relativeLocation: String, mimeType: String, mediaStoreUri: Uri): Uri {

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        }

        var uri: Uri? = null

        return runCatching {
            with(context.contentResolver) {
                insert(mediaStoreUri, values)?.also {
                    uri = it // Keep uri reference so it can be removed on failure

                    openOutputStream(it)?.use { outputStream ->
                        inputStream.use { inputStreamUse ->
                            val buffer = ByteArray(1024)
                            var bytesRead: Int
                            while (inputStreamUse.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                        }
                    } ?: throw IOException("Failed to open output stream.")

                } ?: throw IOException("Failed to create new MediaStore record.")
            }
        }.getOrElse {
            uri?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                context.contentResolver.delete(orphanUri, null, null)
            }

            throw it
        }
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