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

    fun withinTime(time: Calendar): Boolean {
        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinutes)
        }

        val endTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinutes)
        }

        return time.after(startTime) && time.before(endTime)
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