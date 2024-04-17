package com.example.traveler.data

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.util.UUID

@IgnoreExtraProperties
@Parcelize
data class Journal(
    val title : String = "",
    val location : String = "",
    val color : String = "18428167704499191808",
    val startDate : String = "",
    val endDate : String = "",
    var notes : String = "",
    val endDateInMillis : Long = 0L,
    val startDateInMillis : Long = 0L,
    var private : Boolean = false,
    var mostMemorialImage : String = ""
): Parcelable {
    constructor() : this("","","18428167704499191808","","","",0L,0L,false)
}

data class Task(
    val title : String = "",
    val startTime : Long = 0L,
    val endTime : Long = 0L,
    val notes : String = "",
    val remind : String = "",
    val notificationID : String = "",
    val alarmItemHashCode : Int = 0
)