package com.example.traveler.data

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.traveler.MainActivity
import com.example.traveler.R


class AlarmReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent?) {
        //get message
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        println("Alarm triggered $message")

        val id = intent.getStringExtra("UNIQUE_ID") ?: return

        createNotificationChannel(context)

        //build notification
        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("Traveler")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        //show notification
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(id.toInt(), builder.build())
        }

    }

    private fun createNotificationChannel(context: Context){ // for android 8.0 and above
        val channelName = "name"
        val description = "description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            "channel_id",
            channelName,
            importance
        )
        channel.description = description

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
