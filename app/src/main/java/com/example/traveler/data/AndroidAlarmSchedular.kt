package com.example.traveler.data

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.common.hash.HashCode
import java.time.ZoneId
import java.util.UUID

class AndroidAlarmSchedular(
    private val context : Context
): AlarmSchedular{

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", item.message)
            putExtra("UNIQUE_ID", item.id)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time,
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(hashCode : Int) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                hashCode,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}