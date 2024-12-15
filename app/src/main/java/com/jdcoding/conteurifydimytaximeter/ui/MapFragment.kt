package com.jdcoding.conteurifydimytaximeter.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.jdcoding.conteurifydimytaximeter.R
import com.jdcoding.conteurifydimytaximeter.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.jdcoding.conteurifydimytaximeter.DirectionsApiService
import com.jdcoding.conteurifydimytaximeter.DirectionsResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    private var userLocation: Location? = null
    private lateinit var destinationLatLng: LatLng

    // Retrofit service
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val directionsService by lazy { retrofit.create(DirectionsApiService::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Recenter button
        binding.btnRecenter.setOnClickListener {
            recenterMap()
        }

        // Start Trip button
        binding.buttonStartTrip.setOnClickListener {
            startTrip()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableUserLocation()
        setupMapClickListener()
    }

    // Enable User Location on Map
    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.setOnMyLocationChangeListener { location ->
                userLocation = location
                recenterMap()
            }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    // Recenter Map to User's Current Location
    private fun recenterMap() {
        userLocation?.let {
            val userLatLng = LatLng(it.latitude, it.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
        } ?: Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show()
    }

    // Handle Map Click to Select Destination
    private fun setupMapClickListener() {
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear() // Clear previous markers
            destinationLatLng = latLng
            googleMap.addMarker(MarkerOptions().position(latLng).title("Destination"))
            userLocation?.let {
                val origin = "${it.latitude},${it.longitude}"
                val destination = "${latLng.latitude},${latLng.longitude}"
                fetchRoute(origin, destination)
            } ?: Toast.makeText(requireContext(), "Current location not available", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch Route from Directions API
    private fun fetchRoute(origin: String, destination: String) {
        val call = directionsService.getRoute(origin, destination, YOUR_API_KEY)

        call.enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if (response.isSuccessful) {
                    val route = response.body()?.routes?.firstOrNull()
                    route?.let {
                        drawRouteOnMap(it.overview_polyline.points)
                        val leg = it.legs.firstOrNull()
                        leg?.let {
                            updateTripInfo(it.distance.text, it.duration.text)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load route", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Draw Route on Map
    private fun drawRouteOnMap(encodedPoints: String) {
        val points = PolyUtil.decode(encodedPoints)
        val polylineOptions = PolylineOptions()
            .addAll(points)
            .width(10f)
            .color(Color.BLUE)
        googleMap.addPolyline(polylineOptions)
    }

    // Update Trip Info UI
    private fun updateTripInfo(distance: String, duration: String) {
        binding.textDistance.text = distance
        binding.textDuration.text = duration
        binding.tripInfoContainer.visibility = View.VISIBLE // Show trip info
    }

    // Start Trip: Add logic for trip tracking
    private fun startTrip() {
        Toast.makeText(requireContext(), "Trip Started!", Toast.LENGTH_SHORT).show()
        // You can now start tracking the user's location for fare calculation.
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1001
        private const val YOUR_API_KEY = "YOUR_GOOGLE_MAPS_API_KEY"
    }
}
