package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.NotificationReciever
import java.util.*

class WorkoutResetReciever : BroadcastReceiver() {

    // Triggered by the Alarm periodically (starts the service to run task)
    override fun onReceive(context: Context, intent: Intent) {
        val uid = intent.getStringExtra(Constants.UID_TAG)!!
        val ref = FirebaseFirestore.getInstance()
            .collection(Constants.COMPLETED_SESSIONS_COLLECTION)

        ref
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    Log.e("resetting", "resetting it all!")
                    ref.document(document.id).set(CompletedSessions(0, uid))
                }
            }

        setupNotifications(uid, context)
    }

    private fun setupNotifications(uid: String, context: Context) {
        val manager = WorkoutManager(uid, context)
        val intervals = manager.createIntervals()

        intervals.forEach {
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, it.hour)
                set(Calendar.MINUTE, it.minute)
                set(Calendar.SECOND, 0)
            }

            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, NotificationReciever::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 1, alarmIntent, 0)
            alarm.set(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
        }
    }
}