package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings.Setting
import kotlinx.android.parcel.Parcelize

data class Interval(
    var workout: String = "",
    var hour: Int = -1,
    var minute : Int = -1,
    var duration: Long = 0, // in seconds always
    var userId: String = ""

){
    @get:Exclude
    var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Interval {
            val interval = snapshot.toObject(Interval::class.java)!!
            interval.id = snapshot.id
            return interval
        }
    }
}