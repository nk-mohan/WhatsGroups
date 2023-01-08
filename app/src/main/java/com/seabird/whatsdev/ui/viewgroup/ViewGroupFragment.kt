package com.seabird.whatsdev.ui.viewgroup

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentViewGroupBinding
import com.seabird.whatsdev.network.model.GroupModel
import com.seabird.whatsdev.network.other.Status
import com.seabird.whatsdev.parcelable
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.ui.MainActivity
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.AppUtils
import com.seabird.whatsdev.viewmodels.ViewGroupViewModel


class ViewGroupFragment : Fragment() {

    private var _binding: FragmentViewGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewGroupViewModel: ViewGroupViewModel by activityViewModels()

    var groupData: GroupModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        (activity as MainActivity).lockNavigationDrawer()
        _binding = FragmentViewGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupData = arguments?.parcelable(AppConstants.GROUP_DATA)

        groupData?.let {
            binding.groupData = groupData
            viewGroupViewModel.updateViewedStatus(groupData!!.id)
        }

        setObservers()
    }

    private fun setObservers() {

        viewGroupViewModel.reportRes.observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        viewGroupViewModel.resetReportStatus()
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), getString(R.string.group_reported), Toast.LENGTH_SHORT).show()
                    }
                    Status.ERROR -> {
                        viewGroupViewModel.resetReportStatus()
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), AppUtils.getErrorCode(it.code, it.message, requireContext()), Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewGroupViewModel.favouriteStatusUpdated.observe(viewLifecycleOwner) { isFavourite ->
            isFavourite?.let {
                updateFavouriteIcon(it)
                viewGroupViewModel.resetFavouriteStatus()
            }
        }

        binding.viewJoinNow.setSafeOnClickListener {
            try {
                val intentWhatsAppGroup = Intent(Intent.ACTION_VIEW)
                val uri: Uri = Uri.parse(binding.groupLinkTextView.text.toString())
                intentWhatsAppGroup.data = uri
                intentWhatsAppGroup.setPackage("com.whatsapp")
                startActivity(intentWhatsAppGroup)
            } catch (e: ActivityNotFoundException){
                e.printStackTrace()
                Toast.makeText(requireContext(), "WhatsApp Not Installed",Toast.LENGTH_SHORT).show()
            }

        }

        groupData?.let {
            updateFavouriteIcon(viewGroupViewModel.isFavouriteItem(it))
        }

        binding.favorite.setSafeOnClickListener {
            groupData?.let {
                viewGroupViewModel.updateFavouriteItem(it)
            }
        }

        binding.imageCopyGroupLink.setSafeOnClickListener {
            (requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                setPrimaryClip(ClipData.newPlainText("text", binding.groupLinkTextView.text.toString()))
            }
            Toast.makeText(requireContext(), "Link Copied",Toast.LENGTH_SHORT).show()
        }

        binding.imageShareGroupLink.setSafeOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, binding.groupLinkTextView.text.toString())
            startActivity(Intent.createChooser(intent, "Share via"))
        }

        binding.viewReportGroup.setSafeOnClickListener {
            groupData?.let {
                viewGroupViewModel.reportGroup(groupData!!.id)
            }
        }
    }

    private fun updateFavouriteIcon(isFavouriteItem: Boolean) {
        if (isFavouriteItem)
            binding.favorite.setImageResource(R.drawable.ic_favorite)
        else
            binding.favorite.setImageResource(R.drawable.ic_not_favorite)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).unlockNavigationDrawer()
        _binding = null
    }
}