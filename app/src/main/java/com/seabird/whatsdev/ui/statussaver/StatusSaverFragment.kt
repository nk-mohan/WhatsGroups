package com.seabird.whatsdev.ui.statussaver

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.seabird.whatsdev.databinding.FragmentStatusSaverBinding
import com.seabird.whatsdev.utils.PermissionAlertDialog
import com.seabird.whatsdev.utils.PermissionDialogListener
import com.seabird.whatsdev.utils.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StatusSaverFragment : Fragment() {

    private var _binding: FragmentStatusSaverBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val statusSaverViewModel: StatusSaverViewModel by activityViewModels()

    @Inject
    lateinit var permissionAlertDialog: PermissionAlertDialog

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: PermissionManager.isReadFilePermissionAllowed(requireContext())
        if(readPermissionGranted) {
            checkWhatsappFolderPermission()
        }
    }

    private val whatsappPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let { treeUri ->
            requireActivity().contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            statusSaverViewModel.readSDK30(DocumentFile.fromTreeUri(requireContext(), treeUri)!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusSaverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
        checkWhatsappFolderPermission()
    }

    private fun setUpViews() {
        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), requireActivity())

        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        viewPager.currentItem = 0
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = sectionsPagerAdapter.getTabTitle(position)
        }.attach()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun readFilesFromStorage() {
        if (PermissionManager.isReadFilePermissionAllowed(requireContext())
            && PermissionManager.isWriteFilePermissionAllowed(requireContext())) {
            checkWhatsappFolderPermission()
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

    private fun checkWhatsappFolderPermission() {
        if (isWhatsUpInstalled()) {
            val intent: Intent
            val storageManager = requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val statusDir = statusSaverViewModel.getWhatsUpFolder()
            val str = "android.provider.extra.INITIAL_URI"
            if (Build.VERSION.SDK_INT >= 29) {
                intent =  storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
                val scheme = (intent.getParcelableExtra<Uri>(str)).toString().replace("/root/", "/document/")
                val stringBuilder = "$scheme%3A$statusDir"
                intent.putExtra(str, Uri.parse(stringBuilder))
            } else {
                intent = Intent("android.intent.action.OPEN_DOCUMENT_TREE")
                intent.putExtra(str, Uri.parse(statusDir))
            }

            val persistedUriPermissions = requireActivity().contentResolver.persistedUriPermissions
            persistedUriPermissions.forEach {
                if(it.uri.toString().contains(statusDir)){
                    statusSaverViewModel.readSDK30(DocumentFile.fromTreeUri(requireContext(), it.uri)!!)
                    return
                }
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            whatsappPermissionLauncher.launch(intent)
            return
        }
    }

    private fun isWhatsUpInstalled(): Boolean {
        return try {
            requireActivity().packageManager.getPackageInfo("com.whatsapp", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

}