package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes.BlockoutTime
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings.Setting
import java.util.*
import kotlin.collections.ArrayList


class WorkoutManager(var userId: String, var context: Context) {

    private var setting: Setting = Setting()
    private var blockoutTimes: ArrayList<BlockoutTime> = ArrayList()
    var completedSessions: CompletedSessions = CompletedSessions()
    var totalSessions: Int = 500
    var intervals: ArrayList<Interval> = ArrayList()

    private val settingsRef =
        FirebaseFirestore.getInstance().collection(Constants.SETTINGS_COLLECTION)
    private val blockoutTimesRef =
        FirebaseFirestore.getInstance().collection(Constants.BLOCKOUT_TIMES_COLLECTION)
    private val completedSessionsRef =
        FirebaseFirestore.getInstance().collection(Constants.COMPLETED_SESSIONS_COLLECTION)

    init {
        settingsRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.wtf(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }

                if (snapshot!!.documentChanges.size == 0) {
                    setting = Setting()
                    setting.userId = userId

                }

                for (docChange in snapshot!!.documentChanges) {
                    val set = Setting.fromSnapshot(docChange.document)
                    setting = set
                    totalSessions = if (setting.timePerSessionUnit == "seconds") {
                        (setting.totalTime * 60) / setting.timePerSession
                    } else {
                        //minutes
                        (setting.totalTime / setting.timePerSession)
                    }
                    //TODO: update the intervals from here
                    createIntervals()
                }
            }

        blockoutTimesRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.e(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }

                for (docChange in snapshot!!.documentChanges) {
                    val bt = BlockoutTime.fromSnapshot(docChange.document)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            blockoutTimes.add(0, bt)
                        }

                        DocumentChange.Type.REMOVED -> {
                            val pos = blockoutTimes.indexOfFirst { bt.id == it.id }
                            blockoutTimes.removeAt(pos)
                        }

                        DocumentChange.Type.MODIFIED -> {
                            val pos = blockoutTimes.indexOfFirst { bt.id == it.id }
                            blockoutTimes[pos] = bt
                        }
                    }
                    //TODO: update the intervals if needed here
                }
            }
        completedSessionsRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener{snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.wtf(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }

                if (snapshot!!.documentChanges.size == 0) {
                    completedSessions = CompletedSessions()
                    completedSessions.userId = userId

                }

                for (docChange in snapshot!!.documentChanges) {
                    val newCompletedSessions = CompletedSessions.fromSnapshot(docChange.document)
                    completedSessions = newCompletedSessions

                }

            }
    }

    private fun createIntervals() {
        intervals.clear()

        val workouts = getPossibleWorkouts()

        for (i in 1..totalSessions) {
            //TODO change to real times based on blockouts
            var interval = Interval(
                workouts.random(),
                12,
                0,
                if (setting.timePerSessionUnit == "seconds") {
                    setting.timePerSession.toLong()
                } else {
                    (setting.timePerSession * 60).toLong()
                }
            )
            intervals.add(interval)
        }
    }

    fun inBlockoutTime(currentTime: Date): Boolean {
        blockoutTimes.forEach { time ->
            if (time.withinTime(currentTime)) return true
        }
        return false
    }

    fun getCurrentBlockoutTime(): BlockoutTime? {
        val currentTime = Calendar.getInstance().getTime()
        blockoutTimes.sortWith(kotlin.Comparator { first, second ->
            if (first.startHour == second.startHour) {
                first.startMinutes - second.startMinutes
            } else {
                first.startHour - second.startHour
            }
        })

        for (time in blockoutTimes) {
            if (time.withinTime(currentTime)) return time
        }

        return null
    }

    fun getNextBlockoutTime(): BlockoutTime {
        val currentTime = Calendar.getInstance().getTime()
        blockoutTimes.sortWith(kotlin.Comparator { first, second ->
            if (first.startHour == second.startHour) {
                first.startMinutes - second.startMinutes
            } else {
                first.startHour - second.startHour
            }
        })

        for (time in blockoutTimes) {
            if (time.startHour == currentTime.hours) {
                if (time.startMinutes > currentTime.minutes) {
                    return time
                }
            } else if (time.startHour > currentTime.hours) {
                return time
            }

        }

        return blockoutTimes.first()
    }


    private fun getPossibleWorkouts(): List<String> {
        var workouts = ArrayList<String>()

        if (setting.workoutArms) {
            workouts.addAll(Constants.armWorkouts)
        }

        if (setting.workoutCardio) {
            workouts.addAll(Constants.cardioWorkouts)
        }

        if (setting.workoutCore) {
            workouts.addAll(Constants.coreWorkouts)
        }

        if (setting.workoutLegs) {
            workouts.addAll(Constants.legWorkouts)
        }

        return workouts
    }

    fun getPercentCompleted(): Int {
        return ((completedSessions.completedSessions * 100) / totalSessions).toInt()
    }

    fun getIntervalsLeft(): Int {
        return (totalSessions - completedSessions.completedSessions)
    }

    //always in minutes
    fun getTimeLeft(): Int {
        var timeLeft =
            ((totalSessions * setting.timePerSession) - ((totalSessions - getIntervalsLeft()) * setting.timePerSession)).toInt()

        if (setting.timePerSessionUnit == "seconds") {
            timeLeft /= 60
        }
        return timeLeft
    }

    fun getNotification(intent: Intent): Notification {
        val builder = Notification.Builder(context, Constants.CHANNEL_ID)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(context.getString(R.string.notification_message))
        builder.setSmallIcon(R.drawable.weight_icon)
        builder.setChannelId(Constants.CHANNEL_ID)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        return builder.build()
    }

    fun sessionCompleted() {
        completedSessions.completedSessions++
    }

}