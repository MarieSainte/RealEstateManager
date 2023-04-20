package com.example.realestatemanager.services


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.realestatemanager.R


class Notification {

    companion object {
        private const val CHANNEL_ID = "channel1"
        private const val notificationId: Int = 101
        @SuppressLint("ObsoleteSdkInt")
        fun createNotificationChannel(context: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.app_name)
                val descriptionText = context.getString(R.string.New_housing_saved)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
        @SuppressLint("MissingPermission")
        fun sendNotification(context: Context){
            val builder = NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_home_24)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.New_housing_saved))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder.build())
            }
        }
    }
}