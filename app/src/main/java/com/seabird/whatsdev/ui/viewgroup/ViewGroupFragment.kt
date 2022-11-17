package com.seabird.whatsdev.ui.viewgroup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentViewGroupBinding
import com.seabird.whatsdev.network.model.GroupData
import com.seabird.whatsdev.setSafeOnClickListener
import com.seabird.whatsdev.utils.AppConstants


class ViewGroupFragment : Fragment() {

    private var _binding: FragmentViewGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groupData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(AppConstants.GROUP_DATA, GroupData::class.java)
        } else {
            arguments?.getParcelable(AppConstants.GROUP_DATA) as GroupData?
        }

        groupData?.let {
            binding.groupData = groupData
        }

        setObservers()
    }

    private fun setObservers() {
        binding.viewJoinNow.setSafeOnClickListener {
            val intentWhatsAppGroup = Intent(Intent.ACTION_VIEW)
            val uri: Uri = Uri.parse(binding.groupLinkTextView.text.toString())
            intentWhatsAppGroup.data = uri
            intentWhatsAppGroup.setPackage("com.whatsapp")
            startActivity(intentWhatsAppGroup)
        }

        binding.favorite.setSafeOnClickListener {
            binding.favorite.setImageResource(R.drawable.ic_favorite)
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
    }
}