package com.seabird.whatsdev.ui.statussaver

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentStatusImageBinding
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.ui.StatusItemClickListener
import com.seabird.whatsdev.utils.AppConstants


class StatusImageFragment : Fragment() {

    private var _binding: FragmentStatusImageBinding? = null
    private val binding get() = _binding!!

    private val statusSaverViewModel: StatusSaverViewModel by activityViewModels()

    private val statusAdapter: StatusAdapter by lazy { StatusAdapter(statusSaverViewModel.selectedList, statusItemClickListener) }

    private val whatsappPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let { treeUri ->
            requireActivity().contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            statusSaverViewModel.readSDK30(DocumentFile.fromTreeUri(requireContext(), treeUri)!!)
        }
    }

    private val statusItemClickListener = object : StatusItemClickListener {
        override fun setItemClickListener(position: Int) {
            if (statusSaverViewModel.selectedList.isEmpty()) {
                val bundle = Bundle()
                bundle.putInt(AppConstants.MEDIA_POSITION, position)
                bundle.putBoolean(AppConstants.FROM_IMAGE_LIST, true)
                findNavController().navigate(R.id.nav_status_viewer, bundle)
            } else {
                statusSaverViewModel.selectOrDeselectImageItem(position)
                statusAdapter.notifyItemChanged(position)
                (activity as MainActivity).onStatusItemSelected()
            }
        }

        override fun setItemLongClickListener(position: Int) {
            statusSaverViewModel.selectOrDeselectImageItem(position)
            statusAdapter.notifyItemChanged(position)
            (activity as MainActivity).onStatusItemSelected()
        }

    }

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
        checkWhatsappFolderPermission()
    }

    private fun setUpViews() {
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE

        binding.rvStatusImage.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            setEmptyView(null)
            adapter = statusAdapter
            addItemDecoration(GridSpacingItemDecoration(requireContext(), 3))
        }

        binding.emptyView.viewStatus.setSafeOnClickListener {
            val launchIntent: Intent? = requireActivity().packageManager.getLaunchIntentForPackage("com.whatsapp")
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                Toast.makeText(requireContext(), "WhatsApp Not Installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setObservers() {
        statusSaverViewModel.imageList.observe(viewLifecycleOwner) {
            it?.let {
                binding.rvStatusImage.setEmptyView(binding.emptyView.emptyViewLayout)
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                statusAdapter.setMedias(it)
            }
        }

        statusSaverViewModel.clearSelection.observe(viewLifecycleOwner) {
            if (it && statusSaverViewModel.imageList.value != null) {
                val bundle = Bundle()
                bundle.putInt(AppConstants.REFRESH_SELECTION, 1)
                statusAdapter.notifyItemRangeChanged(0, statusSaverViewModel.imageList.value!!.size, bundle)
            }
        }

        statusSaverViewModel.updateList.observe(viewLifecycleOwner) {
            if (it)
                checkWhatsappFolderPermission()
        }

        statusSaverViewModel.whatsappNotInstalled.observe(viewLifecycleOwner) {
            if (it) {
                binding.rvStatusImage.setEmptyView(binding.emptyView.emptyViewLayout)
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
            }

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
        } else {
            statusSaverViewModel.whatsappNotInstalled()
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