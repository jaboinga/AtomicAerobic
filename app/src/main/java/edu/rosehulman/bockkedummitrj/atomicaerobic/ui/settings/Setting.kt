package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import edu.rosehulman.bockkedummitrj.atomicaerobic.Constants

data class Setting(
    var randomization: Boolean = false,
    var timePerSession: Int = Constants.DEFAULT_TIME_PER_SESSION,
    var timePerSessionUnit: String = "minutes",
    var totalTime: Int = Constants.DEFAULT_TOTAL_TIME,
    var userId: String = "",
    var workoutArms: Boolean = true,
    var workoutCardio: Boolean = true,
    var workoutCore: Boolean = true,
    var workoutLegs: Boolean = true
) {
    @get:Exclude
    var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Setting {
            val setting = snapshot.toObject(Setting::class.java)!!
            setting.id = snapshot.id
            return setting
        }
    }
}
