package com.example.traveler.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiClient {
    private const val BASE_URL = "https://countryapi.io/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiInterface = retrofit.create(ApiInterface::class.java)


    private const val BASE_URL_IMAGE = "https://api.pexels.com/v1/"

    private val retrofitImage = Retrofit.Builder()
        .baseUrl(BASE_URL_IMAGE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiServiceImage : ApiInterface = retrofitImage.create(ApiInterface::class.java)



    private const val BASE_URL_WEATHER = "https://api.openweathermap.org/"

    private val retrofitWeather = Retrofit.Builder()
        .baseUrl(BASE_URL_WEATHER)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiServiceWeather : ApiInterface = retrofitWeather.create(ApiInterface::class.java)

    private const val BASE_URL_TOURISM = "https://api.geoapify.com/"

    private val retrofitTourism = Retrofit.Builder()
        .baseUrl(BASE_URL_TOURISM)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiServiceTourism : ApiInterface = retrofitTourism.create(ApiInterface::class.java)
}