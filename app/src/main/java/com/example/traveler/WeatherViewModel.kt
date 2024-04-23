package com.example.traveler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.ApiClient
import com.example.traveler.data.City
import com.example.traveler.data.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel(){


    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData : LiveData<WeatherResponse> get() = _weatherData


    fun fetchData(city : City) {
        viewModelScope.launch{
            try {
                val response = ApiClient.apiServiceWeather.getWeather(city.latitude, city.longitude)
                _weatherData.value = response
                _weatherData.value!!.main.feels_like -= 272.15
                _weatherData.value!!.main.temp -= 272.15
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}