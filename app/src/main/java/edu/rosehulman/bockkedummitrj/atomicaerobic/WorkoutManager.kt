package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.NotificationReciever
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes.BlockoutTime
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings.Setting
import java.util.*
import kotlin.collections.ArrayList


class WorkoutManager(var userId: String, var context: Context) {

    private var setting: Setting = Setting()
    private var blockoutTimes: ArrayList<BlockoutTime> = ArrayList()
    private var completedSessions: CompletedSessions = CompletedSessions()
    private var totalSessions: Int = 500
    private var intervals: ArrayList<Interval> = ArrayList()

    private val settingsRef =
        FirebaseFirestore.getInstance().collection(Constants.SETTINGS_COLLECTION)
    private val blockoutTimesRef =
        FirebaseFirestore.getInstance().collection(Constants.BLOCKOUT_TIMES_COLLECTION)
    private val completedSessionsRef =
        FirebaseFirestore.getInstance().collection(Constants.COMPLETED_SESSIONS_COLLECTION)
    private val intervalsRef =
        FirebaseFirestore.getInstance().collection(Constants.INTERVALS_COLLECTION)
    private var completedSessionsId = "test"

    init {
        settingsRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
//                    Log.wtf(Constants.TAG, "Listen error: $exception")
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
                }
            }

        blockoutTimesRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
//                    Log.e(Constants.TAG, "Listen error: $exception")
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
                }
            }

        completedSessionsRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
//                    Log.wtf(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }

                if (snapshot!!.documentChanges.size == 0) {
                    completedSessions = CompletedSessions()
                    completedSessions.userId = userId
                    completedSessionsRef.add(completedSessions)
                }

                for (docChange in snapshot!!.documentChanges) {
                    val newCompletedSessions = CompletedSessions.fromSnapshot(docChange.document)
                    completedSessions = newCompletedSessions
                    completedSessionsId = completedSessions.id
                }

            }

        intervalsRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
//                    Log.e(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }

                for (docChange in snapshot!!.documentChanges) {
                    val int = Interval.fromSnapshot(docChange.document)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            intervals.add(0, int)
                        }

                        DocumentChange.Type.REMOVED -> {
                            val pos = intervals.indexOfFirst { int.id == it.id }
                            intervals.removeAt(pos)
                        }

                        DocumentChange.Type.MODIFIED -> {
                            val pos = intervals.indexOfFirst { int.id == it.id }
                            intervals[pos] = int
                        }
                    }
                }
            }
    }

    fun createIntervals() {
        getNewSettings()
        //Everything calls each other....
    }

    private fun getNewSettings() {
        settingsRef
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener {
                for (document in it!!.documents) {
                    val doc = Setting.fromSnapshot(document)
                    setting = doc
                }
                removeAllIntervals()
            }

    }

    private fun removeAllIntervals() {

        intervalsRef
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener {
                for (document in it!!.documents) {
                    val doc = Interval.fromSnapshot(document)
                    intervalsRef.document(doc.id).delete()
                }
                createNewIntervals()
            }
    }

    private fun createNewIntervals() {
        val workouts = getPossibleWorkouts()

        for (i in 1..totalSessions) {
            val time = getRandomTime()
            val interval = Interval(
                workouts.random(),
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                if (setting.timePerSessionUnit == "seconds") {
                    setting.timePerSession.toLong()
                } else {
                    (setting.timePerSession * 60).toLong()
                },
                userId
            )
            intervals.add(interval)
            intervalsRef.add(interval)
        }

        NotificationRunnable(intervals, context).run()
    }

    private fun getRandomTime(): Calendar {
        val rightNow = Calendar.getInstance()
        val currentHour = rightNow.get(Calendar.HOUR_OF_DAY)

        while (true) {
            val randomHour = (currentHour..23).random()
            val randomMinute = (0..59).random()

            val interval = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, randomHour)
                set(Calendar.MINUTE, randomMinute)
            }
            if (!inBlockoutTime(interval)) return interval
        }

    }

    class NotificationRunnable(
        private var intervals: ArrayList<Interval>,
        private var context: Context
    ) : Runnable {
        override fun run() {
            var count = 1
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
                    PendingIntent.getBroadcast(context, count++, alarmIntent, 0)
                alarm.setExact(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
            }
        }

    }

    fun inBlockoutTime(currentTime: Calendar): Boolean {
        blockoutTimes.forEach { time ->
            if (time.withinTime(currentTime)) return true
        }
        return false
    }

    fun getCurrentBlockoutTime(): BlockoutTime? {
        val currentTime = Calendar.getInstance()
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
        val currentTime = Calendar.getInstance()
        blockoutTimes.sortWith(kotlin.Comparator { first, second ->
            if (first.startHour == second.startHour) {
                first.startMinutes - second.startMinutes
            } else {
                first.startHour - second.startHour
            }
        })

        for (time in blockoutTimes) {
            if (time.startHour == currentTime.get(Calendar.HOUR_OF_DAY)) {
                if (time.startMinutes > currentTime.get(Calendar.MINUTE)) {
                    return time
                }
            } else if (time.startHour > currentTime.get(Calendar.HOUR_OF_DAY)) {
                return time
            }

        }

        return blockoutTimes.first()
    }


    private fun getPossibleWorkouts(): List<String> {
        val workouts = ArrayList<String>()

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

    fun sessionCompleted() {
        completedSessions.completedSessions++
        completedSessionsRef.document(completedSessionsId).set(completedSessions)
    }

}