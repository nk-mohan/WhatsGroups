package com.seabird.whatsdev.ui.addgroup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.seabird.whatsdev.databinding.FragmentAddGroupBinding
import com.seabird.whatsdev.ui.MainActivity

class AddGroupFragment : Fragment() {

    private var _binding: FragmentAddGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        (activity as MainActivity).hideAddGroupAction()
        _binding = FragmentAddGroupBinding.inflate(inflater, container, false)
        return binding.root
    }
}