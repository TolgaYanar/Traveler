package com.example.traveler

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.ApiClient
import com.example.traveler.data.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CountryViewModel : ViewModel() {

    private val _countryData = MutableLiveData<Country>()
    val countryData : LiveData<Country> get() = _countryData

    val countryList = mutableStateListOf<Country>()

    init {
        listFetchData()
    }

    private fun fetchData() {
        viewModelScope.launch{
            try {
                val response = ApiClient.apiServiceCountry.getCapital(city = "paris")
                _countryData.value = response.id

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun listFetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            val countriesToFetch = listOf("ankara", "berlin")//"ankara", "berlin", "tokyo", "london","canberra","Washington, D.C.", "kabul","amsterdam","moscow","brussels"
            for (countryName in countriesToFetch) {
                try {
                    val country = ApiClient.apiServiceCountry.getCapital(countryName)
                    val image = ApiClient.apiServiceImage.getImage(countryName).photos.get(0).src.original
                    country.id.imageUrl = image
                    countryList.add(country.id)
                } catch (e: Exception) {
                    // Handle error, such as logging or showing a toast
                    e.printStackTrace()
                }
            }
        }
    }
}