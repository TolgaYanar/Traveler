package com.example.traveler.data

import com.google.common.hash.HashCode
import java.util.UUID

interface AlarmSchedular {
    fun schedule(item: AlarmItem)
    fun cancel(hashCode: Int)
}