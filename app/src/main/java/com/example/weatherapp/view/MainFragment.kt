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
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.viewModel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var locationKey: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocation()
    }

    override fun onResume() {
        super.onResume()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.btnPronostico.setOnClickListener() { showFiveDayForecast() }
        return view
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
            mainViewModel.getLocationKey(Constants.apiKey,
                coordinates, Constants.language, false)
        }
        Log.i("LocationInfo", "getCurrentConditions")
        mainViewModel.getLocationKeyResult().observe(this) { key ->
            Log.i("Location key", key.Key)
            this.locationKey = key.Key
            binding.appTitle.text = key.LocalizedName
            getWeatherForecast(this.locationKey)
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

    private fun getWeatherForecast(locationKey: String) {
        lifecycleScope.launch {
            mainViewModel.getCurrentConditions(locationKey, Constants.apiKey,
                Constants.language, false)
        }
        mainViewModel.getCurrentCondtionsResult().observe(viewLifecycleOwner) { data ->
            Log.i("Forecast", data.WeatherText + " Fragment")
            setupUI(data)
        }
    }

    private fun setupUI(forecast: WeatherForecast) {
        binding.dateText.text = forecast.LocalObservationDateTime.split("T")[0]
        binding.weatherText.text = forecast.WeatherText
        val temperatureText =
            forecast.Temperature.Metric.Value.toString() + " º" + forecast.Temperature.Metric.Unit
        binding.temperatureText.text = temperatureText
        val resourceSrc = "image" + forecast.WeatherIcon
        binding.weatherImage.setImageResource(getResources().getIdentifier(resourceSrc, "drawable", requireActivity().packageName))
        if (forecast.HasPrecipitation) {
            if (forecast.PrecipitationType != null) {
                binding.precipitationText.text = forecast.PrecipitationType
            } else {
                binding.precipitationText.text = "Precipitaciones"
            }
        } else {
            binding.precipitationText.text = "Sin precipitaciones"
        }
    }

    private fun showFiveDayForecast() {
        findNavController().navigate(R.id.fiveDayForecastFragment)
    }
}