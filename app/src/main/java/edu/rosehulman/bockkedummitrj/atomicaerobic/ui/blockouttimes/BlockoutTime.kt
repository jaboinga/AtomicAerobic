package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class BlockoutTime(
    var startHour: Int = 0,
    var startMinutes: Int = 0,
    var endHour: Int = 0,
    var endMinutes: Int = 0,
    var userId: String = ""
) {

    fun withinTime(time: Date): Boolean {
        Log.wtf("after", time.after(
            Date(
                time.year,
                time.month,
                time.day,
                startHour,
                startMinutes
            )).toString())

        Log.wtf("before", (time.before(Date(time.year, time.month, time.day, endHour, endMinutes)).toString()))

        Log.wtf("current hour", time.hours.toString())

        return time.after(
            Date(
                time.year,
                time.month,
                time.day,
                startHour,
                startMinutes
            )
        ) && time.before(Date(time.year, time.month, time.day, endHour, endMinutes))

    }

    @get:Exclude
    var id = ""
    @ServerTimestamp
    var creation: Timestamp? = null

    companion object {
        const val CREATION_KEY = "creation"
        fun fromSnapshot(snapshot: DocumentSnapshot): BlockoutTime {
            val bt = snapshot.toObject(BlockoutTime::class.java)!!
            bt.id = snapshot.id
            return bt
        }
    }
}