package com.example.traveler

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.ApiClient
import com.example.traveler.data.ApiKey
import com.example.traveler.data.City
import com.example.traveler.data.PopularCities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CountryViewModel : ViewModel() {

    val cityList = mutableStateListOf<City>()

    init {
        listCityFetchData()
    }


    private fun listCityFetchData() {
        viewModelScope.launch(Dispatchers.IO) {

            val citiesToFetch = PopularCities.allPopularCities.shuffled().subList(0,15)
            for (cityName in citiesToFetch) {
                try {
                    val city = ApiClient.apiServiceCity.getCity(cityName)
                    val image = ApiClient.apiServiceImage.getImage(cityName).photos.get(0).src.original
                    city.get(0).imageUrl = image
                    cityList.add(city.get(0))
                } catch (e: Exception) {
                    // Handle error, such as logging or showing a toast
                    e.printStackTrace()

                    if (e is HttpException && (e.code() == 401 || e.code() == 400)) {
                        ApiKey.rotateCityApiKey()
                    }
                }
            }
        }
    }

    fun fetchParticularCityData(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val city = ApiClient.apiServiceCity.getCity(cityName)
                val image = ApiClient.apiServiceImage.getImage(cityName).photos.get(0).src.original
                city.get(0).imageUrl = image
                var notExist = true
                cityList.forEach { compareCity->
                    if(compareCity.name == city.get(0).name){
                        notExist = false
                    }
                }

                if(notExist){
                    cityList.add(city.get(0))
                }

            } catch (e: Exception) {
                // Handle error, such as logging or showing a toast
                e.printStackTrace()

                if (e is HttpException && (e.code() == 401 || e.code() == 400)) {
                    ApiKey.rotateCityApiKey()
                }
            }

        }
    }
}