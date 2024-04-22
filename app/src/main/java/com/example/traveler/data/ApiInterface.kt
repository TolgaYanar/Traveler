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
        @Header("Authorization") apiKey: String = "8mA6ag3hncORGS8uahovbiZPMbVMGNg9NOMURz4IZinZXQ6HijZXTD5U"): PhotoResponse

    //weather api
    @GET("data/2.5/weather")
    suspend fun getWeather(@Query("lat") lat : Double,
                           @Query("lon") lon : Double,
                           @Query("appid") appid : String = "16f3d29a19e372f77ae7cdce77df685a"): WeatherResponse

    //tourism api
    @GET("v2/places")
    suspend fun getTourist(@Query("categories") categories : String, //tourism, catering.restaurant
                           @Query("filter") filter : String, //circle:32.83780387262269,39.92079185,10000
                           @Query("limit") limit : Int,
                           @Query("apiKey") apikey : String = "29d783588ad44be8963bd1789aef538f"): TourismResponse

    @GET("v1/city")
    suspend fun getCity(@Query("name") city: String,
                         @Header("X-Api-Key") apiKey: String = "QOjO5qbsSSVIAKhTDn1qJg==jgxl0Mb2U6yxVyMk"): List<City>
}
