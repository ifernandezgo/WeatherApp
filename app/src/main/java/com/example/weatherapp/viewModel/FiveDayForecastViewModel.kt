package com.example.weatherapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.APIService
import com.example.weatherapp.model.FiveDayForecast
import com.example.weatherapp.model.Key
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltViewModel
class FiveDayForecastViewModel @Inject constructor(savedStateHandle: SavedStateHandle)
    : ViewModel() {

    private val locationKey = MutableLiveData<Key>()
    private val fiveDayForecast = MutableLiveData<FiveDayForecast>()

    fun getLocationKeyResult(): LiveData<Key> = locationKey
    fun getFiveDayForecastResult(): LiveData<FiveDayForecast> = fiveDayForecast

    private val repository: Repository by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dataservice.accuweather.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(APIService::class.java)
        Repository(service)
    }

    suspend fun getLocationKey(apiKey: String, coordinates: String,
                               language: String, details: Boolean) {
        viewModelScope.launch {
            val key = repository.getLocationKey(apiKey, coordinates, language, details)
            locationKey.value = key
        }
    }

    suspend fun getFiveDayForecast(locationKey: String, apiKey: String, language: String, details: Boolean, metric: Boolean) {
        viewModelScope.launch {
            val forecast = repository.getFiveDayForecast(locationKey, apiKey, language, details, metric)
            fiveDayForecast.value = forecast
        }
    }
}