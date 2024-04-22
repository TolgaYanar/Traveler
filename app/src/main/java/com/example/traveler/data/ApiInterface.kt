package com.example.traveler.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    //country api
    @GET("capital/{city}")
    suspend fun getCapital(@Path("city") city: String,
        @Query("apikey") apiKey: String = "4u5S5JGs1mzvhDFpFlo5q5XYdrfgsrFem1hCmNg2"): CountryResponse

    //image api
    @GET("search")
    suspend fun getImage(@Query("query") search: String,
        @Header("Authorization") apiKey: String = ApiKey.getImageApiKey()): PhotoResponse

    //weather api
    @GET("data/2.5/weather")
    suspend fun getWeather(@Query("lat") lat : Double,
                           @Query("lon") lon : Double,
                           @Query("appid") appid : String = ApiKey.getWeatherApiKey()): WeatherResponse

    //tourism api
    @GET("v2/places")
    suspend fun getTourist(@Query("categories") categories : String, //tourism, catering.restaurant
                           @Query("filter") filter : String, //circle:32.83780387262269,39.92079185,10000
                           @Query("limit") limit : Int,
                           @Query("apiKey") apikey : String = ApiKey.getTouristApiKey()): TourismResponse


    @GET("v1/city")
    suspend fun getCity(@Query("name") city: String,
                         @Header("X-Api-Key") apiKey: String = ApiKey.getCityApiKey()): List<City>
}
