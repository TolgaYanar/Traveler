package com.example.traveler.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var fullName: String = "",
    var email: String = "",
    var about: String = "Describe yourself with your own words!",
    var profile_image: String = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
    var followers : Int = 0,
    val following : Int = 0,
    var uid : String = "",
    var uri : String = "",
    var ongoing_trip : String = ""
    ): Parcelable
