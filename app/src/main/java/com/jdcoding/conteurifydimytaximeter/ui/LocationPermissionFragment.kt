package com.jdcoding.conteurifydimytaximeter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jdcoding.conteurifydimytaximeter.R
import com.jdcoding.conteurifydimytaximeter.databinding.FragmentLocationPermissionBinding

class LocationPermissionFragment : Fragment() {

    private var _binding: FragmentLocationPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.proceedToMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_locationPermission_to_map)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
