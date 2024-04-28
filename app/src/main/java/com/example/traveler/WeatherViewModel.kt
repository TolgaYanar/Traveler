package com.example.traveler

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.data.ApiClient
import com.example.traveler.data.City
import com.example.traveler.data.Forecast
import com.example.traveler.data.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel(){


    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData : LiveData<WeatherResponse> get() = _weatherData

    private val _fiveDayForecastData = MutableLiveData<List<Forecast>>()

    fun fetchData(city : City) {
        viewModelScope.launch{
            try {
                val weatherResponse = ApiClient.apiServiceWeather.getWeather(city.latitude, city.longitude)
                _weatherData.value = weatherResponse
                _weatherData.value!!.main.feels_like -= 272.15
                _weatherData.value!!.main.temp -= 272.15

                val forecastResponse = ApiClient.apiServiceWeather.getTenDayForecast(city.latitude, city.longitude)
                _fiveDayForecastData.value = forecastResponse.list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @Composable
    fun TenDayForecast(onDismissRequest: () -> Unit, journalPropertiesViewModel: JournalPropertiesViewModel){

        Dialog(onDismissRequest = {
            onDismissRequest()
        }) {

            Surface {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    if(_fiveDayForecastData.value != null){

                        items(_fiveDayForecastData.value!!){forecast->

                            Row(modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically)
                            {
                                Column(modifier = Modifier.padding(5.dp)) {
                                    Text(text = "MON", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text(text = forecast.dt_txt)
                                }

                                Image(painter = painterResource(id = getDrawableResourceId("icon${
                                    forecast.weather.get(0).icon}")), contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                )

                                Text(text = forecast.weather.get(0).description, modifier = Modifier.padding(5.dp),
                                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Divider(Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }

}