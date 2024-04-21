package com.example.traveler.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CountryResponse(
    @SerializedName("tr", alternate = ["jp", "us","de","fr","gb","au","af","nl","be","ru"])
    val id: Country
)

@Parcelize
data class Country(
    @SerializedName("name")
    val name: String = "",

    @SerializedName("region")
    val region: String = "",

    @SerializedName("flag")
    val flag: Flag = Flag(),

    @SerializedName("capital")
    val capital: String = "",

    var imageUrl: String = "",

    @SerializedName("latLng")
    val latlng: latLng
    // Add other fields you need here
): Parcelable

@Parcelize
data class Flag(
    @SerializedName("small")
    val small: String = "",
    @SerializedName("medium")
    val medium: String = "",
    @SerializedName("large")
    val large: String = "",
): Parcelable

@Parcelize
data class latLng(
    @SerializedName("capital")
    val capital: List<Double>,
    @SerializedName("country")
    val country: List<Double>
): Parcelable

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
