package com.seabird.whatsdev.ui.statussaver

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
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

    private val statusSaverViewModel by lazy { viewModels<StatusSaverViewModel>() }

    @Inject
    lateinit var permissionAlertDialog: PermissionAlertDialog

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: PermissionManager.isReadFilePermissionAllowed(requireContext())
        if(readPermissionGranted) {

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
        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), requireActivity().supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        readFilesFromStorage()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun readFilesFromStorage() {
        if (PermissionManager.isReadFilePermissionAllowed(requireContext())
            && PermissionManager.isWriteFilePermissionAllowed(requireContext())) {

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

}