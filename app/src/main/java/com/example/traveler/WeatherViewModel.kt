package com.example.traveler

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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
    fun FiveDayForecast(onDismissRequest: () -> Unit, allowedIndexes: MutableList<Int>, dialogStatus: MutableState<Boolean>){

        Dialog(onDismissRequest = {
            onDismissRequest()
        }) {

            Surface {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    if(_fiveDayForecastData.value != null){

                        itemsIndexed(_fiveDayForecastData.value!!){ index, forecast ->

                            if(allowedIndexes.contains(index)){

                                val date = LocalDateTime.ofEpochSecond(forecast.dt, 0, ZoneOffset.UTC)

                                Row(modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically)
                                {
                                    Column(modifier = Modifier.padding(5.dp)) {

                                        if(date.hour == 0) Text(text = forecast.dt_txt.dropLast(9),
                                            modifier = Modifier.padding(bottom = 3.dp))

                                        Text(text = date.dayOfWeek.name.substring(0,3), fontSize = 20.sp, fontWeight = FontWeight.Bold)

                                        Text(text =
                                        if(date.hour<10){
                                            "0"
                                        } else {
                                            ""
                                        }
                                                + "${date.hour}:00 " +
                                                if(date.hour>=12)
                                                    "PM" else
                                                    "AM")

                                    }

                                    Image(painter = painterResource(id = getDrawableResourceId("icon${
                                        forecast.weather.get(0).icon}")), contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                    )

                                    Text(text = forecast.weather.get(0).description, modifier = Modifier
                                        .padding(3.dp)
                                        .widthIn(max = 70.dp),
                                        fontSize = 16.sp, fontWeight = FontWeight.Bold)

                                    if(date.hour == 0){
                                        IconButton(onClick = {
                                            if(allowedIndexes.contains(index+1)){
                                                allowedIndexes.removeAll(
                                                    listOf(index+1,
                                                        index+2,
                                                        index+3,
                                                        index+4,
                                                        index+5,
                                                        index+6,
                                                        index+7
                                                    )
                                                )
                                            }else{
                                                allowedIndexes.addAll(
                                                    listOf(index+1,
                                                        index+2,
                                                        index+3,
                                                        index+4,
                                                        index+5,
                                                        index+6,
                                                        index+7
                                                    )
                                                )
                                            }
                                            allowedIndexes.sortBy{
                                                it
                                            }

                                            dialogStatus.value = false
                                            dialogStatus.value = true
                                        })
                                        {
                                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                                        }
                                    }
                                }

                                Divider(Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }

}