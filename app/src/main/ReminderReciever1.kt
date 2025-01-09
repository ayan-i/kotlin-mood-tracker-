package com.example.mobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("reminder_message") ?: "Time to take your medication!"
        val medicationName = intent.getStringExtra("medication_name") ?: "Medication"

        val notification = NotificationCompat.Builder(context, "medication_reminder")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(medicationName)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (notificationManager.areNotificationsEnabled()) {
                    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
                }
            } else {
                notificationManager.notify(System.currentTimeMillis().toInt(), notification)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}