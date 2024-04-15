package com.example.traveler

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.ApiClient
import com.example.traveler.data.Country
import com.example.traveler.data.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel(){


    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData : LiveData<WeatherResponse> get() = _weatherData


    fun fetchData(country: Country) {
        viewModelScope.launch{
            try {
                val response = ApiClient.apiServiceWeather.getWeather(country.latlng.capital[0], country.latlng.capital[1])
                _weatherData.value = response
                _weatherData.value!!.main.feels_like -= 272.15
                _weatherData.value!!.main.temp -= 272.15
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}