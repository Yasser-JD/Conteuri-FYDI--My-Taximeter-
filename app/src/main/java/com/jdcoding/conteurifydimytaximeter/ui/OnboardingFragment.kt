package com.jdcoding.conteurifydimytaximeter.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jdcoding.conteurifydimytaximeter.R
import com.jdcoding.conteurifydimytaximeter.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    // Launcher for requesting permissions
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                navigateToLocationPermissionPage()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager
        val onboardingItems = listOf(
            OnboardingItem(R.drawable.ic_logo, "Welcome to Taximeter App"),
            OnboardingItem(R.drawable.ic_maps, "Track Your Location"),
            OnboardingItem(R.drawable.ic_taxi, "Allow Location Permission")
        )

        viewPager.adapter = OnboardingAdapter(onboardingItems)

        binding.buttonNext.setOnClickListener {
            if (viewPager.currentItem < onboardingItems.size - 1) {
                viewPager.currentItem += 1
            } else if (viewPager.currentItem == onboardingItems.size - 1) {
                checkLocationPermission()
            }
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted, navigate to location permission page
                navigateToLocationPermissionPage()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Show rationale and request permission
                Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                // Directly request permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun navigateToLocationPermissionPage() {
        findNavController().navigate(R.id.action_onboarding_to_locationPermission)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
