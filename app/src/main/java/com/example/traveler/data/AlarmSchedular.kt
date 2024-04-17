package com.example.traveler.data

import java.util.UUID

interface AlarmSchedular {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}