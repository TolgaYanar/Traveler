package com.example.traveler.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<Weather>
)

data class Main(
    @SerializedName("feels_like")
    var feels_like: Double,
//    val grnd_level: Int,
//    val humidity: Int,
//    val pressure: Int,
//    val sea_level: Int,
    @SerializedName("temp")
    var temp: Double,
//    val temp_max: Double,
//    val temp_min: Double
)

data class Weather(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
    //icon
    //val url : String = "https://openweathermap.org/img/wn/$icon@2x.png"
)