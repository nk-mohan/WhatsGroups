package com.seabird.whatsdev.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seabird.whatsdev.BuildConfig
import com.seabird.whatsdev.R
import com.seabird.whatsdev.databinding.FragmentSettingsBinding
import com.seabird.whatsdev.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        (activity as MainActivity).hideAddGroupAction()
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpListener()
    }

    private fun setUpListener() {
        binding.moreAppsLayout.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Sea+Bird+Developer")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Sea+Bird+Developer")))
            }
        }

        binding.privacyLayout.setOnClickListener {
            findNavController().navigate(R.id.nav_privacy_policy)
        }
    }

    private fun setUpViews() {
        binding.textVersionName.text = versionName()
    }

    private fun versionName(): String {
        val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val stringBuilder = StringBuilder()
            .append("Released On: ")
            .append(dateFormat.format(BuildConfig.BUILD_TIME))
            .append(System.getProperty("line.separator"))
            .append(String.format(getString(R.string.version_name),
                BuildConfig.VERSION_NAME))
        val versionDate = dateFormat.format(BuildConfig.BUILD_TIME)
        val versionName = BuildConfig.VERSION_NAME
        val firstIndex = stringBuilder.indexOf(versionDate)
        val secondIndex = stringBuilder.indexOf(versionName)
        val spannableString = SpannableString(stringBuilder)
        val bold = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(bold, firstIndex, versionDate.length + firstIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.BLACK), firstIndex, versionDate.length + firstIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(bold, secondIndex, versionName.length + secondIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.BLACK), secondIndex, versionName.length + secondIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}