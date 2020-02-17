package edu.rosehulman.bockkedummitrj.atomicaerobic.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import edu.rosehulman.bockkedummitrj.atomicaerobic.Constants
import edu.rosehulman.bockkedummitrj.atomicaerobic.Interval
import edu.rosehulman.bockkedummitrj.atomicaerobic.MainActivity
import edu.rosehulman.bockkedummitrj.atomicaerobic.R
import java.util.*

class NotificationReciever : BroadcastReceiver() {
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        val displayIntent = Intent(context, MainActivity::class.java)
        val notification = getNotification(displayIntent, context)
        Log.e("notification reciever", "sending notification")
        notificationManager.notify(101, notification)

    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(Constants.CHANNEL_ID, "Atomic Aerobic", importance)
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(channel)
    }

    private fun getNotification(intent: Intent, context: Context): Notification {
        val builder = Notification.Builder(context, Constants.CHANNEL_ID)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(context.getString(R.string.notification_message))
        builder.setSmallIcon(R.drawable.weight_icon)
        builder.setAutoCancel(true)
        builder.setChannelId(Constants.CHANNEL_ID)
        val pendingIntent =
            PendingIntent.getActivity(context, 2, intent, 0)
        builder.setContentIntent(pendingIntent)
        return builder.build()
    }
}