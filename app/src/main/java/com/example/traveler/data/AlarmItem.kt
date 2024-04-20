package com.example.traveler.data

import java.util.UUID

data class AlarmItem(
    val notified: Long = 0L,
    val message: String = "",
    val id: String = "",
    val startTime : Long = 0L,
    val title : String = "",
    var seen : Boolean = false
)

