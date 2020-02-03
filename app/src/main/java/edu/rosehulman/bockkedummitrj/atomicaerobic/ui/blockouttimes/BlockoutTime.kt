package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class BlockoutTime(
    var startTime: String? = "",
    var endTime: String? = "",
    var timePeriod: String? = "",
    var userId: String? = ""
){

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