package com.example.traveler.data

import com.google.gson.annotations.SerializedName

data class TourismResponse(
    @SerializedName("features")
    val features : List<Tourism>
)

data class Tourism(
    @SerializedName("properties")
    val properties : Properties
)

data class Properties(
    @SerializedName("name")
    val name : String,
    @SerializedName("city")
    val city : String,
    @SerializedName("district")
    val district : String
)
