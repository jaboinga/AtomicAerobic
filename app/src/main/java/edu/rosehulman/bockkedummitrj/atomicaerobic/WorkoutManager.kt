package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.content.Context
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
                    completedSessionsRef.add(setting)
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

    fun createIntervals(): ArrayList<Interval> {
        settingsRef
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->

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

                removeAllIntervals()
                val workouts = getPossibleWorkouts()

                for (i in 1..totalSessions) {
                    val interval = Interval(
                        workouts.random(),
                        21,
                        53 + i,
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
            }

        return intervals
    }

    private fun removeAllIntervals() {
        intervals.forEach {
            intervalsRef.document(it.id).delete()
        }

    }

    fun inBlockoutTime(currentTime: Date): Boolean {
        blockoutTimes.forEach { time ->
            //TODO this is a little broken maybe
            if (time.withinTime(currentTime)) return true
        }
        return false
    }

    fun getCurrentBlockoutTime(): BlockoutTime? {
        //TODO this is a little broken maybe
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
        //TODO this is a little broken maybe
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

    fun sessionCompleted() {
        completedSessions.completedSessions++
        completedSessionsRef.document(completedSessionsId).set(completedSessions)
    }

}