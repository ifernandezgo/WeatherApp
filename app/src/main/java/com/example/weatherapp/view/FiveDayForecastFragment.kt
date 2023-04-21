package com.example.weatherapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.Constants
import com.example.weatherapp.databinding.FragmentFiveDayForecastBinding
import com.example.weatherapp.viewModel.FiveDayForecastViewModel
import com.example.weatherapp.viewModel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FiveDayForecastFragment : Fragment() {

    private var _binding: FragmentFiveDayForecastBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationKey: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val fiveDayForecastViewModel: FiveDayForecastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        super.onCreate(savedInstanceState)
        getCurrentLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFiveDayForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getCurrentLocation() {
        if(checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
                return
            }
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                Log.i("LocationInfo", location.latitude.toString()+","+location.longitude.toString())
                getCurrentConditions(location)
            }

        } else {
            requestPermission()
        }
    }

    private fun getCurrentConditions(location: Location) {
        val coordinates: String = location.latitude.toString()+","+location.longitude.toString()
        lifecycleScope.launch{
            fiveDayForecastViewModel.getLocationKey(Constants.apiKey,
                coordinates, Constants.language, false)
        }
        Log.i("LocationInfo", "getCurrentConditions")
        fiveDayForecastViewModel.getLocationKeyResult().observe(this) { key ->
            Log.i("Location key FiveDayFragment ", key.Key)
            this.locationKey = key.Key
            getFiveDayForecast(this.locationKey)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }
        }
    }

    private fun getFiveDayForecast(locationKey: String) {
        lifecycleScope.launch {
            fiveDayForecastViewModel.getFiveDayForecast(locationKey, Constants.apiKey,
                Constants.language, false, true)
        }
        fiveDayForecastViewModel.getFiveDayForecastResult().observe(viewLifecycleOwner) { data ->
            binding.rvForecast.adapter = ForecastAdapter(data.DailyForecasts)
        }
    }
}