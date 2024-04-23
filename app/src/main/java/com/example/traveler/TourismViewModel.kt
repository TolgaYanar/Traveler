package com.example.traveler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.ApiClient
import com.example.traveler.data.City
import com.example.traveler.data.Tourism
import kotlinx.coroutines.launch

class TourismViewModel : ViewModel() {

    private val _tourismData = MutableLiveData<List<Tourism>>()
    val tourismData : LiveData<List<Tourism>> get() = _tourismData

    private val _restaurantData = MutableLiveData<List<Tourism>>()
    val restaurantData : LiveData<List<Tourism>> get() = _restaurantData

    fun fetchData(city : City) {
        viewModelScope.launch{
            //circle:32.83780387262269,39.92079185,10000
            val filter = "circle:${city.longitude},${city.latitude},5000"
            try {
                val tourism_response = ApiClient.apiServiceTourism.getTourist(filter = filter, categories = "tourism",
                    limit = 10)
                _tourismData.value = tourism_response.features
                val catering_response = ApiClient.apiServiceTourism.getTourist(filter = filter, categories = "catering.restaurant",
                    limit = 10)
                _restaurantData.value = catering_response.features
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}