package com.example.traveler.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("country")
    val country: String = "",
    @SerializedName("population")
    val population: Int = 0,
    @SerializedName("is_capital")
    val is_capital: Boolean = false,
    var imageUrl: String = "",
): Parcelable
